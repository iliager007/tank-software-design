package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.TankController;
import ru.mipt.bit.platformer.model.TankView;
import ru.mipt.bit.platformer.model.WorldView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class PlayerTankController implements TankController {

    private final ActionSource actionSource;
    private final List<MovementBinding> movementBindings;

    public PlayerTankController(ActionSource actionSource, List<MovementBinding> movementBindings) {
        this.actionSource = actionSource;
        this.movementBindings = Collections.unmodifiableList(new ArrayList<>(movementBindings));
    }

    @Override
    public Optional<Direction> chooseDirection(TankView tank, WorldView world) {
        Set<PlayerAction> actions = actionSource.getPressedActions();
        for (MovementBinding binding : movementBindings) {
            if (actions.contains(binding.getAction())) {
                return Optional.of(binding.getDirection());
            }
        }
        return Optional.empty();
    }
}
