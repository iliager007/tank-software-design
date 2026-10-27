package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MoveTankCommand;
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
        return Optional.of(new MoveTankCommand(directions[random.nextInt(directions.length)]));
    }
}
