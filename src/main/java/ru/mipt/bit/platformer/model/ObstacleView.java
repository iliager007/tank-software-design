package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Domain: контракт чтения препятствия в составе порта WorldView.
 */
public interface ObstacleView extends GameObjectView {

    GridPoint2 getCoordinates();
}
