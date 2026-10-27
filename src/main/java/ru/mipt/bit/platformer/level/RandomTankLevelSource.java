package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.WorldSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public final class RandomTankLevelSource implements LevelSource {

    private final LevelSource source;
    private final int tankCount;
    private final Random random;

    public RandomTankLevelSource(LevelSource source, int tankCount, Random random) {
        if (tankCount < 0) {
            throw new IllegalArgumentException("AI tank count must not be negative");
        }
        this.source = Objects.requireNonNull(source, "source");
        this.tankCount = tankCount;
        this.random = Objects.requireNonNull(random, "random");
    }

    @Override
    public LevelDefinition load() throws IOException {
        LevelDefinition level = source.load();
        if ((long) level.getWidth() * level.getHeight() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Level must fit in an integer cell count");
        }
        WorldSettings settings = level.getWorldSettings();
        List<GridPoint2> aiTanks = settings.getAiTankCoordinates();
        Set<GridPoint2> occupied = new HashSet<>(settings.getObstacleCoordinates());
        occupied.add(settings.getTankCoordinates());
        occupied.addAll(aiTanks);
        List<GridPoint2> freeCells = new ArrayList<>();
        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                GridPoint2 cell = new GridPoint2(x, y);
                if (!occupied.contains(cell)) {
                    freeCells.add(cell);
                }
            }
        }
        Collections.shuffle(freeCells, random);
        aiTanks.addAll(freeCells.subList(0, Math.min(tankCount, freeCells.size())));
        return new LevelDefinition(level.getWidth(), level.getHeight(), new WorldSettings(
                settings.getTankCoordinates(), settings.getTankMovementDuration(),
                settings.getObstacleCoordinates(), aiTanks));
    }
}
