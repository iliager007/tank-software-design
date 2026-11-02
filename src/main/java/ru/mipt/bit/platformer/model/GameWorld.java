package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class GameWorld implements WorldContext, ObservableWorld {

    private final int width;
    private final int height;
    private final Tank tank;
    private final List<GameObject> objects = new ArrayList<>();
    private final List<WorldListener> listeners = new ArrayList<>();
    private final MovementPolicy movementPolicy;
    private boolean advancingObjects;

    public GameWorld() {
        this(WorldSettings.standard(), new CollisionMovementPolicy());
    }

    public GameWorld(WorldSettings settings, MovementPolicy movementPolicy) {
        this(10, 8, settings, movementPolicy);
    }

    public GameWorld(int width, int height, WorldSettings settings, MovementPolicy movementPolicy) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.movementPolicy = Objects.requireNonNull(movementPolicy, "movementPolicy");
        Set<GridPoint2> occupied = new HashSet<>();
        reserveInitialCell(settings.getTankCoordinates(), occupied);
        tank = createTank(settings.getTankCoordinates(), settings);
        addObject(tank);
        for (GridPoint2 coordinates : settings.getAiTankCoordinates()) {
            reserveInitialCell(coordinates, occupied);
            addObject(createTank(coordinates, settings));
        }
        for (GridPoint2 coordinates : settings.getObstacleCoordinates()) {
            reserveInitialCell(coordinates, occupied);
            addObject(new Obstacle(coordinates));
        }
    }

    public void update(TankController tankController, float deltaTime) {
        update(tankController, (aiTank, world) -> Optional.empty(), deltaTime);
    }

    public void update(TankController playerController, TankController aiController, float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Tick must be finite and not negative");
        }
        List<Runnable> commands = new ArrayList<>();
        for (GameObject object : new ArrayList<>(objects)) {
            if (object instanceof Tank) {
                Tank currentTank = (Tank) object;
                TankController controller = currentTank == tank ? playerController : aiController;
                Optional<TankCommand> chosen = currentTank.isMovementComplete()
                        ? controller.chooseCommand(currentTank, this)
                        : controller.chooseCommandWhileMoving(currentTank, this);
                chosen.ifPresent(command -> commands.add(() -> {
                    if (contains(currentTank)) {
                        command.execute(currentTank, movementPolicy, this);
                    }
                }));
            }
        }
        commands.forEach(Runnable::run);
        for (BulletView bullet : getBullets()) {
            resolveCollision((Bullet) bullet);
        }
        advanceObjects(deltaTime);
    }

    private void advanceObjects(float deltaTime) {
        double remainingTime = deltaTime;
        do {
            Map<Bullet, GridPoint2> bulletOrigins = new LinkedHashMap<>();
            double step = remainingTime;
            for (GameObject object : objects) {
                if (object instanceof Bullet) {
                    Bullet bullet = (Bullet) object;
                    bulletOrigins.put(bullet, bullet.getCurrentCoordinates());
                    step = Math.min(step, bullet.getTimeUntilNextStep());
                }
            }
            advancingObjects = true;
            try {
                for (GameObject object : new ArrayList<>(objects)) {
                    if (contains(object)) {
                        object.live((float) step, this);
                    }
                }
            } finally {
                advancingObjects = false;
            }
            resolveBulletCollisions(bulletOrigins);
            for (Bullet bullet : bulletOrigins.keySet()) {
                resolveCollision(bullet);
            }
            remainingTime = Math.max(0d, remainingTime - step);
        } while (remainingTime > 0d);
    }

    private void resolveBulletCollisions(Map<Bullet, GridPoint2> origins) {
        List<Bullet> bullets = new ArrayList<>(origins.keySet());
        Set<Bullet> collided = new HashSet<>();
        for (int i = 0; i < bullets.size(); i++) {
            Bullet first = bullets.get(i);
            if (!contains(first)) {
                continue;
            }
            for (int j = i + 1; j < bullets.size(); j++) {
                Bullet second = bullets.get(j);
                if (contains(second) && (first.occupies(second.getCurrentCoordinates())
                        || first.occupies(origins.get(second)) && second.occupies(origins.get(first)))) {
                    collided.add(first);
                    collided.add(second);
                }
            }
        }
        collided.forEach(this::removeObject);
    }

    @Override
    public void addObject(GameObject object) {
        Objects.requireNonNull(object, "object");
        if (!objects.contains(object)) {
            objects.add(object);
            for (WorldListener listener : new ArrayList<>(listeners)) {
                listener.objectAdded(object);
            }
        }
    }

    @Override
    public void removeObject(GameObject object) {
        if (objects.remove(object)) {
            for (WorldListener listener : new ArrayList<>(listeners)) {
                listener.objectRemoved(object);
            }
        }
    }

    @Override
    public boolean contains(GameObject object) {
        return objects.contains(object);
    }

    @Override
    public boolean resolveCollision(Bullet bullet) {
        if (!contains(bullet)) {
            return true;
        }
        if (advancingObjects) {
            return false;
        }
        GridPoint2 coordinates = bullet.getCurrentCoordinates();
        if (!isInside(coordinates)) {
            removeObject(bullet);
            return true;
        }
        for (GameObject object : new ArrayList<>(objects)) {
            if (bullet.canHit(object) && object.occupies(coordinates)) {
                object.hit(bullet, this);
                removeObject(bullet);
                return true;
            }
        }
        return false;
    }

    @Override
    public void addListener(WorldListener listener) {
        Objects.requireNonNull(listener, "listener");
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            for (GameObject object : new ArrayList<>(objects)) {
                listener.objectAdded(object);
            }
        }
    }

    @Override
    public void removeListener(WorldListener listener) {
        listeners.remove(listener);
    }

    private Tank createTank(GridPoint2 coordinates, WorldSettings settings) {
        return new Tank(coordinates, settings.getTankMovementDuration(),
                settings.getTankHealth(), settings.getBulletSettings());
    }

    private boolean isInside(GridPoint2 coordinates) {
        return coordinates.x >= 0 && coordinates.x < width && coordinates.y >= 0 && coordinates.y < height;
    }

    private void reserveInitialCell(GridPoint2 coordinates, Set<GridPoint2> occupied) {
        if (!isInside(coordinates) || !occupied.add(coordinates)) {
            throw new IllegalArgumentException("Objects must occupy distinct cells inside the level: " + coordinates);
        }
    }

    private <T extends GameObjectView> List<T> objectsOfType(Class<T> type) {
        return Collections.unmodifiableList(objects.stream().filter(type::isInstance)
                .map(type::cast).collect(Collectors.toList()));
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public TankView getTank() {
        return tank;
    }

    @Override
    public List<TankView> getTanks() {
        return objectsOfType(TankView.class);
    }

    @Override
    public List<ObstacleView> getObstacles() {
        return objectsOfType(ObstacleView.class);
    }

    @Override
    public List<BulletView> getBullets() {
        return objectsOfType(BulletView.class);
    }

    @Override
    public List<GameObjectView> getObjects() {
        return Collections.unmodifiableList(new ArrayList<>(objects));
    }
}
