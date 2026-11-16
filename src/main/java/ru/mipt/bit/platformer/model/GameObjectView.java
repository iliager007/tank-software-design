package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Domain: контракт объекта входящего порта чтения мира.
 */
public interface GameObjectView {

    boolean occupies(GridPoint2 coordinates);
}
