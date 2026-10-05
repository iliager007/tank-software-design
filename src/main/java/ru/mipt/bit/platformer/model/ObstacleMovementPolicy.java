package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public final class ObstacleMovementPolicy implements MovementPolicy {

    @Override
    public boolean canEnter(GridPoint2 destination, WorldView world) {
        for (ObstacleView obstacle : world.getObstacles()) {
            if (obstacle.getCoordinates().equals(destination)) {
                return false;
            }
        }
        return true;
    }
}
