package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface MovementPolicy {

    boolean canEnter(GridPoint2 destination, WorldView world);
}
