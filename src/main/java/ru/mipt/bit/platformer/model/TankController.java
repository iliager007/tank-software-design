package ru.mipt.bit.platformer.model;

import java.util.Optional;

public interface TankController {

    Optional<TankCommand> chooseCommand(TankView tank, WorldView world);
}
