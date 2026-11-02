package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface GameObjectView {

    boolean occupies(GridPoint2 coordinates);
}
