package ru.mipt.bit.platformer.model;

import java.util.Optional;

public interface TankController {

    Optional<Direction> chooseDirection(TankView tank, WorldView world);
}
