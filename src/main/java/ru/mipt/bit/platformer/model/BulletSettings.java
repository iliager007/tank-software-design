package ru.mipt.bit.platformer.model;

public final class BulletSettings {

    private final int damage;
    private final float movementDuration;

    public BulletSettings(int damage, float movementDuration) {
        if (damage <= 0 || !Float.isFinite(movementDuration) || movementDuration <= 0f) {
            throw new IllegalArgumentException("Bullet damage and movement duration must be positive");
        }
        this.damage = damage;
        this.movementDuration = movementDuration;
    }

    public static BulletSettings standard() {
        return new BulletSettings(50, 0.1f);
    }

    public int getDamage() {
        return damage;
    }

    public float getMovementDuration() {
        return movementDuration;
    }
}
