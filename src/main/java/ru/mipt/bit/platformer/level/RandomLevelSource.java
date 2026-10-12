package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.WorldSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class RandomLevelSource implements LevelSource {

    private final int width;
    private final int height;
    private final int obstacleCount;
    private final float tankMovementDuration;
    private final Random random;

    public RandomLevelSource(int width, int height, int obstacleCount,
                             float tankMovementDuration, Random random) {
        if (width <= 0 || height <= 0 || (long) width * height > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Level dimensions must be positive and fit in an integer cell count");
        }
        if (obstacleCount < 0 || obstacleCount >= (long) width * height) {
            throw new IllegalArgumentException("Obstacle count must leave at least one cell for the player");
        }
        this.width = width;
        this.height = height;
        this.obstacleCount = obstacleCount;
        this.tankMovementDuration = tankMovementDuration;
        this.random = Objects.requireNonNull(random, "random");
    }

    @Override
    public LevelDefinition load() {
        List<GridPoint2> cells = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells.add(new GridPoint2(x, y));
            }
        }
        Collections.shuffle(cells, random);
        WorldSettings settings = new WorldSettings(cells.get(0), tankMovementDuration,
                cells.subList(1, obstacleCount + 1));
        return new LevelDefinition(width, height, settings);
    }
}
