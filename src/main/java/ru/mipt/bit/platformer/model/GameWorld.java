package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class GameWorld implements WorldView {

    private final int width;
    private final int height;
    private final Tank tank;
    private final List<Tank> tanks;
    private final List<TankView> tankViews;
    private final List<ObstacleView> obstacles;
    private final MovementPolicy movementPolicy;

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
        Set<GridPoint2> occupied = new HashSet<>();
        reserveInitialCell(settings.getTankCoordinates(), occupied);
        this.tank = new Tank(settings.getTankCoordinates(), settings.getTankMovementDuration());
        List<Tank> createdTanks = new ArrayList<>();
        createdTanks.add(tank);
        for (GridPoint2 coordinates : settings.getAiTankCoordinates()) {
            reserveInitialCell(coordinates, occupied);
            createdTanks.add(new Tank(coordinates, settings.getTankMovementDuration()));
        }
        this.tanks = Collections.unmodifiableList(createdTanks);
        this.tankViews = Collections.unmodifiableList(new ArrayList<>(createdTanks));
        List<ObstacleView> createdObstacles = new ArrayList<>();
        for (GridPoint2 coordinates : settings.getObstacleCoordinates()) {
            reserveInitialCell(coordinates, occupied);
            createdObstacles.add(new Obstacle(coordinates));
        }
        this.obstacles = Collections.unmodifiableList(createdObstacles);
        this.movementPolicy = movementPolicy;
    }

    public void update(TankController tankController, float deltaTime) {
        update(tankController, (aiTank, world) -> Optional.empty(), deltaTime);
    }

    public void update(TankController playerController, TankController aiController, float deltaTime) {
        // Reserve destinations before advancing any tank, so both cells stay occupied for the whole step.
        for (Tank currentTank : tanks) {
            if (currentTank.isMovementComplete()) {
                TankController controller = currentTank == tank ? playerController : aiController;
                controller.chooseCommand(currentTank, this)
                        .ifPresent(command -> command.execute(currentTank, movementPolicy, this));
            }
        }
        for (Tank currentTank : tanks) {
            currentTank.update(deltaTime);
        }
    }

    private void reserveInitialCell(GridPoint2 coordinates, Set<GridPoint2> occupied) {
        if (coordinates.x < 0 || coordinates.x >= width || coordinates.y < 0 || coordinates.y >= height
                || !occupied.add(coordinates)) {
            throw new IllegalArgumentException("Objects must occupy distinct cells inside the level: " + coordinates);
        }
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
        return tankViews;
    }

    @Override
    public List<ObstacleView> getObstacles() {
        return obstacles;
    }
}
