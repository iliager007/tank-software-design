package ru.mipt.bit.platformer.model;

/**
 * Domain: исходящий порт событий добавления и удаления игровых объектов.
 */
public interface WorldListener {

    void objectAdded(GameObjectView object);

    void objectRemoved(GameObjectView object);
}
