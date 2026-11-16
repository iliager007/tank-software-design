package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Domain: внутренняя стратегия проходимости, не порт внешней системы.
 */
public interface MovementPolicy {

    boolean canEnter(GridPoint2 destination, WorldView world);
}
