package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Objects;

import static com.badlogic.gdx.math.MathUtils.clamp;
import static com.badlogic.gdx.math.MathUtils.isEqual;

public final class Tank implements TankView, GameObject {

    private final float movementDuration;
    private final GridPoint2 currentCoordinates;
    private final GridPoint2 destinationCoordinates;
    private final BulletSettings bulletSettings;

    private float movementProgress = 1f;
    private Direction direction = Direction.RIGHT;
    private int health;
    private float shotCooldown;

    public Tank(GridPoint2 initialCoordinates, float movementDuration) {
        this(initialCoordinates, movementDuration, 100, BulletSettings.standard());
    }

    public Tank(GridPoint2 initialCoordinates, float movementDuration, int health, BulletSettings bulletSettings) {
        if (!Float.isFinite(movementDuration) || movementDuration <= 0f || health < 0) {
            throw new IllegalArgumentException("Movement duration must be positive and health must not be negative");
        }
        this.currentCoordinates = new GridPoint2(initialCoordinates);
        this.destinationCoordinates = new GridPoint2(initialCoordinates);
        this.movementDuration = movementDuration;
        this.health = health;
        this.bulletSettings = Objects.requireNonNull(bulletSettings, "bulletSettings");
    }

    public void tryMove(Direction direction, MovementPolicy movementPolicy, WorldView world) {
        if (!isMovementComplete()) {
            return;
        }

        this.direction = direction;
        GridPoint2 requestedDestination = direction.move(currentCoordinates);
        if (!movementPolicy.canEnter(new GridPoint2(requestedDestination), world)) {
            return;
        }

        destinationCoordinates.set(requestedDestination);
        movementProgress = 0f;
    }

    public void update(float deltaTime) {
        shotCooldown = Math.max(0f, shotCooldown - deltaTime);
        movementProgress = clamp(movementProgress + deltaTime / movementDuration, 0f, 1f);
        if (isMovementComplete()) {
            currentCoordinates.set(destinationCoordinates);
        }
    }

    public void shoot(WorldContext world) {
        if (health < 0 || shotCooldown > movementDuration * 1e-6d || !world.contains(this)) {
            return;
        }
        shotCooldown = movementDuration;
        Bullet bullet = new Bullet(direction.move(currentCoordinates), direction, bulletSettings, this);
        world.addObject(bullet);
        world.resolveCollision(bullet);
    }

    @Override
    public void live(float tick, WorldContext world) {
        update(tick);
    }

    @Override
    public void hit(Bullet bullet, WorldContext world) {
        health -= bullet.getDamage();
        if (health < 0) {
            world.removeObject(this);
        }
    }

    @Override
    public boolean occupies(GridPoint2 coordinates) {
        return currentCoordinates.equals(coordinates) || destinationCoordinates.equals(coordinates);
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public GridPoint2 getCurrentCoordinates() {
        return new GridPoint2(currentCoordinates);
    }

    @Override
    public GridPoint2 getDestinationCoordinates() {
        return new GridPoint2(destinationCoordinates);
    }

    @Override
    public float getMovementProgress() {
        return movementProgress;
    }

    @Override
    public float getRotation() {
        return direction.getRotation();
    }

    boolean isMovementComplete() {
        return isEqual(movementProgress, 1f);
    }
}
