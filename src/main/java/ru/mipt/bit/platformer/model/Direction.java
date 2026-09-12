package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public enum Direction {
    UP(0, 1, 90f),
    LEFT(-1, 0, -180f),
    DOWN(0, -1, -90f),
    RIGHT(1, 0, 0f);

    private final int horizontalOffset;
    private final int verticalOffset;
    private final float rotation;

    Direction(int horizontalOffset, int verticalOffset, float rotation) {
        this.horizontalOffset = horizontalOffset;
        this.verticalOffset = verticalOffset;
        this.rotation = rotation;
    }

    public GridPoint2 move(GridPoint2 coordinates) {
        return new GridPoint2(coordinates).add(horizontalOffset, verticalOffset);
    }

    public float getRotation() {
        return rotation;
    }
}
