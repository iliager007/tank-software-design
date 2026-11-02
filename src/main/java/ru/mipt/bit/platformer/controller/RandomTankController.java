package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MoveTankCommand;
import ru.mipt.bit.platformer.model.ShootTankCommand;
import ru.mipt.bit.platformer.model.TankCommand;
import ru.mipt.bit.platformer.model.TankController;
import ru.mipt.bit.platformer.model.TankView;
import ru.mipt.bit.platformer.model.WorldView;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public final class RandomTankController implements TankController {

    private final Random random;

    public RandomTankController(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    @Override
    public Optional<TankCommand> chooseCommand(TankView tank, WorldView world) {
        Direction[] directions = Direction.values();
        int action = random.nextInt(directions.length + 1);
        return Optional.of(action == directions.length ? new ShootTankCommand() : new MoveTankCommand(directions[action]));
    }
}
