package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WorldSettings {

    private final GridPoint2 tankCoordinates;
    private final float tankMovementDuration;
    private final List<GridPoint2> obstacleCoordinates;

    public WorldSettings(GridPoint2 tankCoordinates, float tankMovementDuration, List<GridPoint2> obstacleCoordinates) {
        this.tankCoordinates = new GridPoint2(tankCoordinates);
        this.tankMovementDuration = tankMovementDuration;
        List<GridPoint2> coordinates = new ArrayList<>(obstacleCoordinates.size());
        for (GridPoint2 coordinate : obstacleCoordinates) {
            coordinates.add(new GridPoint2(coordinate));
        }
        this.obstacleCoordinates = Collections.unmodifiableList(coordinates);
    }

    public static WorldSettings standard() {
        return new WorldSettings(new GridPoint2(1, 1), 0.4f,
                Collections.singletonList(new GridPoint2(1, 3)));
    }

    public GridPoint2 getTankCoordinates() {
        return new GridPoint2(tankCoordinates);
    }

    public float getTankMovementDuration() {
        return tankMovementDuration;
    }

    public List<GridPoint2> getObstacleCoordinates() {
        List<GridPoint2> coordinates = new ArrayList<>(obstacleCoordinates.size());
        for (GridPoint2 coordinate : obstacleCoordinates) {
            coordinates.add(new GridPoint2(coordinate));
        }
        return coordinates;
    }
}
