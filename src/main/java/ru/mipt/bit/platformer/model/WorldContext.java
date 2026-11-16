package ru.mipt.bit.platformer.model;

/**
 * Domain: внутренний контракт взаимодействия сущности с миром, без внешнего I/O.
 */
public interface WorldContext extends WorldView {

    void addObject(GameObject object);

    void removeObject(GameObject object);

    boolean contains(GameObject object);

    boolean resolveCollision(Bullet bullet);
}
