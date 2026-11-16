package ru.mipt.bit.platformer.model;

/**
 * Domain: внутренний контракт состояния танка, не архитектурный порт.
 */
public interface TankState {

    float getMovementSpeedMultiplier();

    default float getFireRateMultiplier() {
        return 1f;
    }

    default boolean canShoot() {
        return true;
    }

    default TankState onHealthChanged(int health, int maxHealth) {
        return this;
    }
}
