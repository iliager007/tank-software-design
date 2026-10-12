package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.WorldSettings;

import java.util.Objects;

public final class LevelDefinition {

    private final int width;
    private final int height;
    private final WorldSettings worldSettings;

    public LevelDefinition(int width, int height, WorldSettings worldSettings) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.worldSettings = Objects.requireNonNull(worldSettings, "worldSettings");
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public WorldSettings getWorldSettings() {
        return worldSettings;
    }
}
