package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Domain: контракт чтения танка в составе порта WorldView.
 */
public interface TankView extends GameObjectView {

    int getHealth();

    int getMaxHealth();

    GridPoint2 getCurrentCoordinates();

    GridPoint2 getDestinationCoordinates();

    float getMovementProgress();

    float getRotation();
}
