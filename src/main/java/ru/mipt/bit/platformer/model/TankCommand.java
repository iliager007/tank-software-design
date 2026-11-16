package ru.mipt.bit.platformer.model;

/**
 * Domain: внутренний контракт команды танка, не входной порт application use-case.
 */
@FunctionalInterface
public interface TankCommand {

    void execute(Tank tank, MovementPolicy movementPolicy, WorldContext world);
}
