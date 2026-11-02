package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface ObstacleView extends GameObjectView {

    GridPoint2 getCoordinates();
}
