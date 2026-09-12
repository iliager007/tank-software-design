package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Collection;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.continueProgress;

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

    public void tryMove(Direction direction, Collection<Obstacle> obstacles) {
        if (!isMovementComplete()) {
            return;
        }

        rotation = direction.getRotation();
        GridPoint2 requestedDestination = direction.move(currentCoordinates);
        if (isObstacleAt(requestedDestination, obstacles)) {
            return;
        }

        destinationCoordinates.set(requestedDestination);
        movementProgress = 0f;
    }

    public void update(float deltaTime) {
        movementProgress = continueProgress(movementProgress, deltaTime, movementDuration);
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

    private boolean isObstacleAt(GridPoint2 requestedDestination, Collection<Obstacle> obstacles) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.isLocatedAt(requestedDestination)) {
                return true;
            }
        }
        return false;
    }
}
