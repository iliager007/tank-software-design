package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface TankView extends GameObjectView {

    int getHealth();

    GridPoint2 getCurrentCoordinates();

    GridPoint2 getDestinationCoordinates();

    float getMovementProgress();

    float getRotation();
}
