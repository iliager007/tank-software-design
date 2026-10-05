package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameWorld implements WorldView {

    private final Tank tank;
    private final List<ObstacleView> obstacles;
    private final MovementPolicy movementPolicy;

    public GameWorld() {
        this(WorldSettings.standard(), new ObstacleMovementPolicy());
    }

    public GameWorld(WorldSettings settings, MovementPolicy movementPolicy) {
        this.tank = new Tank(settings.getTankCoordinates(), settings.getTankMovementDuration());
        List<ObstacleView> createdObstacles = new ArrayList<>();
        for (GridPoint2 coordinates : settings.getObstacleCoordinates()) {
            createdObstacles.add(new Obstacle(coordinates));
        }
        this.obstacles = Collections.unmodifiableList(createdObstacles);
        this.movementPolicy = movementPolicy;
    }

    public void update(TankController tankController, float deltaTime) {
        tankController.chooseDirection(tank, this)
                .ifPresent(direction -> tank.tryMove(direction, movementPolicy, this));
        tank.update(deltaTime);
    }

    @Override
    public TankView getTank() {
        return tank;
    }

    @Override
    public List<ObstacleView> getObstacles() {
        return obstacles;
    }
}
