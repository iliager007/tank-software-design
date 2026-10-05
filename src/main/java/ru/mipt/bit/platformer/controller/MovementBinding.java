package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.model.Direction;

public final class MovementBinding {

    private final PlayerAction action;
    private final Direction direction;

    public MovementBinding(PlayerAction action, Direction direction) {
        this.action = action;
        this.direction = direction;
    }

    public PlayerAction getAction() {
        return action;
    }

    public Direction getDirection() {
        return direction;
    }
}
