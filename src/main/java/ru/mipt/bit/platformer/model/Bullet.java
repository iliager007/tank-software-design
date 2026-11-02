package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Objects;

public final class Bullet implements BulletView, GameObject {

    private final GridPoint2 coordinates;
    private final Direction direction;
    private final BulletSettings settings;
    private final TankView owner;
    private double elapsedTime;

    public Bullet(GridPoint2 coordinates, Direction direction, BulletSettings settings) {
        this(coordinates, direction, settings, null);
    }

    public Bullet(GridPoint2 coordinates, Direction direction, BulletSettings settings, TankView owner) {
        this.coordinates = new GridPoint2(coordinates);
        this.direction = Objects.requireNonNull(direction, "direction");
        this.settings = Objects.requireNonNull(settings, "settings");
        this.owner = owner;
    }

    public boolean canHit(GameObjectView object) {
        return object != this && object != owner;
    }

    @Override
    public void live(float tick, WorldContext world) {
        if (!world.contains(this) || world.resolveCollision(this)) {
            return;
        }
        elapsedTime += tick;
        while (elapsedTime >= settings.getMovementDuration() * (1d - 1e-6)) {
            elapsedTime = Math.max(0d, elapsedTime - settings.getMovementDuration());
            coordinates.set(direction.move(coordinates));
            if (world.resolveCollision(this)) {
                return;
            }
        }
    }

    @Override
    public void hit(Bullet bullet, WorldContext world) {
        world.removeObject(this);
    }

    public int getDamage() {
        return settings.getDamage();
    }

    double getTimeUntilNextStep() {
        return Math.max(0d, settings.getMovementDuration() - elapsedTime);
    }

    @Override
    public boolean occupies(GridPoint2 otherCoordinates) {
        return coordinates.equals(otherCoordinates);
    }

    @Override
    public GridPoint2 getCurrentCoordinates() {
        return new GridPoint2(coordinates);
    }

    @Override
    public GridPoint2 getDestinationCoordinates() {
        return direction.move(coordinates);
    }

    @Override
    public float getMovementProgress() {
        return (float) (elapsedTime / settings.getMovementDuration());
    }

    @Override
    public Direction getDirection() {
        return direction;
    }
}
