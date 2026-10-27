package ru.mipt.bit.platformer.model;

import java.util.Objects;

public final class MoveTankCommand implements TankCommand {

    private final Direction direction;

    public MoveTankCommand(Direction direction) {
        this.direction = Objects.requireNonNull(direction, "direction");
    }

    @Override
    public void execute(Tank tank, MovementPolicy movementPolicy, WorldView world) {
        tank.tryMove(direction, movementPolicy, world);
    }
}
