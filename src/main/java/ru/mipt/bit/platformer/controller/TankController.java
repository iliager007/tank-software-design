package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.TankView;
import ru.mipt.bit.platformer.model.WorldView;

import java.util.Optional;

public interface TankController {

    Optional<Direction> chooseDirection(TankView tank, WorldView world);
}
