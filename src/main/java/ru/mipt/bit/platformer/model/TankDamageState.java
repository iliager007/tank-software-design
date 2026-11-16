package ru.mipt.bit.platformer.model;

/**
 * Domain: состояния урона и переходы между ними по доле начального здоровья.
 */
public enum TankDamageState implements TankState {
    LIGHT(1f, true),
    MEDIUM(0.5f, true),
    HEAVY(1f / 3f, false);

    private final float movementSpeedMultiplier;
    private final boolean shootingAllowed;

    TankDamageState(float movementSpeedMultiplier, boolean shootingAllowed) {
        this.movementSpeedMultiplier = movementSpeedMultiplier;
        this.shootingAllowed = shootingAllowed;
    }

    public static TankDamageState fromHealth(int health, int maxHealth) {
        long scaledHealth = 100L * health;
        if (maxHealth == 0 || scaledHealth < 15L * maxHealth) {
            return HEAVY;
        }
        return scaledHealth <= 70L * maxHealth ? MEDIUM : LIGHT;
    }

    @Override
    public float getMovementSpeedMultiplier() {
        return movementSpeedMultiplier;
    }

    @Override
    public boolean canShoot() {
        return shootingAllowed;
    }

    @Override
    public TankState onHealthChanged(int health, int maxHealth) {
        return fromHealth(health, maxHealth);
    }
}
