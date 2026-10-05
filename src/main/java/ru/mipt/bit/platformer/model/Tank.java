package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import static com.badlogic.gdx.math.MathUtils.clamp;
import static com.badlogic.gdx.math.MathUtils.isEqual;

public final class Tank implements TankView {

    private final float movementDuration;
    private final GridPoint2 currentCoordinates;
    private final GridPoint2 destinationCoordinates;

    private float movementProgress = 1f;
    private float rotation;

    public Tank(GridPoint2 initialCoordinates, float movementDuration) {
        this.currentCoordinates = new GridPoint2(initialCoordinates);
        this.destinationCoordinates = new GridPoint2(initialCoordinates);
        this.movementDuration = movementDuration;
    }

    public void tryMove(Direction direction, MovementPolicy movementPolicy, WorldView world) {
        if (!isMovementComplete()) {
            return;
        }

        rotation = direction.getRotation();
        GridPoint2 requestedDestination = direction.move(currentCoordinates);
        if (!movementPolicy.canEnter(new GridPoint2(requestedDestination), world)) {
            return;
        }

        destinationCoordinates.set(requestedDestination);
        movementProgress = 0f;
    }

    public void update(float deltaTime) {
        movementProgress = clamp(movementProgress + deltaTime / movementDuration, 0f, 1f);
        if (isMovementComplete()) {
            currentCoordinates.set(destinationCoordinates);
        }
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
        return rotation;
    }

    private boolean isMovementComplete() {
        return isEqual(movementProgress, 1f);
    }
}
