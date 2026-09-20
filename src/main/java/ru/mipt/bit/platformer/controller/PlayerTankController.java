package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.input.PlayerAction;
import ru.mipt.bit.platformer.input.PlayerInput;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.TankView;
import ru.mipt.bit.platformer.model.WorldView;

import java.util.EnumSet;
import java.util.Optional;

public final class PlayerTankController implements TankController {

    private final PlayerInput playerInput;

    public PlayerTankController(PlayerInput playerInput) {
        this.playerInput = playerInput;
    }

    @Override
    public Optional<Direction> chooseDirection(TankView tank, WorldView world) {
        EnumSet<PlayerAction> actions = playerInput.getPressedActions();
        if (actions.contains(PlayerAction.MOVE_UP)) {
            return Optional.of(Direction.UP);
        }
        if (actions.contains(PlayerAction.MOVE_LEFT)) {
            return Optional.of(Direction.LEFT);
        }
        if (actions.contains(PlayerAction.MOVE_DOWN)) {
            return Optional.of(Direction.DOWN);
        }
        if (actions.contains(PlayerAction.MOVE_RIGHT)) {
            return Optional.of(Direction.RIGHT);
        }
        return Optional.empty();
    }
}
