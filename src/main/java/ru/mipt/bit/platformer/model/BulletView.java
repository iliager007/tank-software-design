package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface BulletView extends GameObjectView {

    GridPoint2 getCurrentCoordinates();

    GridPoint2 getDestinationCoordinates();

    float getMovementProgress();

    Direction getDirection();
}
