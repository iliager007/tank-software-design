package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public final class CollisionMovementPolicy implements MovementPolicy {

    @Override
    public boolean canEnter(GridPoint2 destination, WorldView world) {
        if (destination.x < 0 || destination.x >= world.getWidth()
                || destination.y < 0 || destination.y >= world.getHeight()) {
            return false;
        }
        for (ObstacleView obstacle : world.getObstacles()) {
            if (obstacle.getCoordinates().equals(destination)) {
                return false;
            }
        }
        for (TankView tank : world.getTanks()) {
            if (tank.getCurrentCoordinates().equals(destination)
                    || tank.getDestinationCoordinates().equals(destination)) {
                return false;
            }
        }
        return true;
    }
}
