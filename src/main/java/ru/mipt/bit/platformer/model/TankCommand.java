package ru.mipt.bit.platformer.model;

@FunctionalInterface
public interface TankCommand {

    void execute(Tank tank, MovementPolicy movementPolicy, WorldView world);
}
