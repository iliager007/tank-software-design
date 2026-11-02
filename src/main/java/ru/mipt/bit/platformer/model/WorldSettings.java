package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class WorldSettings {

    private final GridPoint2 tankCoordinates;
    private final float tankMovementDuration;
    private final List<GridPoint2> obstacleCoordinates;
    private final List<GridPoint2> aiTankCoordinates;
    private final int tankHealth;
    private final BulletSettings bulletSettings;

    public WorldSettings(GridPoint2 tankCoordinates, float tankMovementDuration, List<GridPoint2> obstacleCoordinates) {
        this(tankCoordinates, tankMovementDuration, obstacleCoordinates, Collections.emptyList());
    }

    public WorldSettings(GridPoint2 tankCoordinates, float tankMovementDuration,
                         List<GridPoint2> obstacleCoordinates, List<GridPoint2> aiTankCoordinates) {
        this(tankCoordinates, tankMovementDuration, obstacleCoordinates, aiTankCoordinates,
                100, BulletSettings.standard());
    }

    public WorldSettings(GridPoint2 tankCoordinates, float tankMovementDuration,
                         List<GridPoint2> obstacleCoordinates, List<GridPoint2> aiTankCoordinates,
                         int tankHealth, BulletSettings bulletSettings) {
        if (tankHealth < 0 || !Float.isFinite(tankMovementDuration) || tankMovementDuration <= 0f) {
            throw new IllegalArgumentException("Health must not be negative and movement duration must be positive");
        }
        this.tankCoordinates = new GridPoint2(tankCoordinates);
        this.tankMovementDuration = tankMovementDuration;
        this.obstacleCoordinates = Collections.unmodifiableList(copyCoordinates(obstacleCoordinates));
        this.aiTankCoordinates = Collections.unmodifiableList(copyCoordinates(aiTankCoordinates));
        this.tankHealth = tankHealth;
        this.bulletSettings = Objects.requireNonNull(bulletSettings, "bulletSettings");
    }

    public int getTankHealth() {
        return tankHealth;
    }

    public BulletSettings getBulletSettings() {
        return bulletSettings;
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
        return copyCoordinates(obstacleCoordinates);
    }

    public List<GridPoint2> getAiTankCoordinates() {
        return copyCoordinates(aiTankCoordinates);
    }

    private static List<GridPoint2> copyCoordinates(List<GridPoint2> source) {
        List<GridPoint2> coordinates = new ArrayList<>(source.size());
        for (GridPoint2 coordinate : source) {
            coordinates.add(new GridPoint2(coordinate));
        }
        return coordinates;
    }
}
