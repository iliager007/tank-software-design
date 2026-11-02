package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public final class Obstacle implements ObstacleView, GameObject {

    private final GridPoint2 coordinates;

    public Obstacle(GridPoint2 coordinates) {
        this.coordinates = new GridPoint2(coordinates);
    }

    public boolean isLocatedAt(GridPoint2 otherCoordinates) {
        return coordinates.equals(otherCoordinates);
    }

    @Override
    public boolean occupies(GridPoint2 otherCoordinates) {
        return isLocatedAt(otherCoordinates);
    }

    @Override
    public void live(float tick, WorldContext world) {
    }

    @Override
    public void hit(Bullet bullet, WorldContext world) {
    }

    @Override
    public GridPoint2 getCoordinates() {
        return new GridPoint2(coordinates);
    }
}
