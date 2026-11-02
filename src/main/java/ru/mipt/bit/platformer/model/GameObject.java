package ru.mipt.bit.platformer.model;

public interface GameObject extends GameObjectView {

    void live(float tick, WorldContext world);

    void hit(Bullet bullet, WorldContext world);
}
