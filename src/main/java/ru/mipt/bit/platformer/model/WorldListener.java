package ru.mipt.bit.platformer.model;

public interface WorldListener {

    void objectAdded(GameObjectView object);

    void objectRemoved(GameObjectView object);
}
