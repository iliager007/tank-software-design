package ru.mipt.bit.platformer.model;

import java.util.Optional;

/**
 * Domain: исходящий порт выбора команд; мир запрашивает решения через этот контракт.
 */
public interface TankController {

    Optional<TankCommand> chooseCommand(TankView tank, WorldView world);

    default Optional<TankCommand> chooseCommandWhileMoving(TankView tank, WorldView world) {
        return Optional.empty();
    }
}
