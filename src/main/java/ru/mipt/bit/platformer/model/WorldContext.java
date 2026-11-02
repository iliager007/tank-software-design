package ru.mipt.bit.platformer.model;

public interface WorldContext extends WorldView {

    void addObject(GameObject object);

    void removeObject(GameObject object);

    boolean contains(GameObject object);

    boolean resolveCollision(Bullet bullet);
}
