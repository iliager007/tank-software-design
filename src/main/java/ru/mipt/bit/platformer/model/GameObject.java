package ru.mipt.bit.platformer.model;

/**
 * Domain: внутренний контракт жизненного цикла сущности, не архитектурный порт.
 */
public interface GameObject extends GameObjectView {

    void live(float tick, WorldContext world);

    void hit(Bullet bullet, WorldContext world);
}
