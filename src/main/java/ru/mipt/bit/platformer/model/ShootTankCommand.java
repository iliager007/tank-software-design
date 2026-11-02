package ru.mipt.bit.platformer.model;

public final class ShootTankCommand implements TankCommand {

    @Override
    public void execute(Tank tank, MovementPolicy movementPolicy, WorldContext world) {
        tank.shoot(world);
    }
}
