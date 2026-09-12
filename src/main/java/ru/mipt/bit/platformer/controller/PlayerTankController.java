package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.input.PlayerInput;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.TankView;
import ru.mipt.bit.platformer.model.WorldView;

import java.util.Optional;

public final class PlayerTankController implements TankController {

    private final PlayerInput playerInput;

    public PlayerTankController(PlayerInput playerInput) {
        this.playerInput = playerInput;
    }

    @Override
    public Optional<Direction> chooseDirection(TankView tank, WorldView world) {
        return playerInput.getRequestedDirection();
    }
}
