package ru.mipt.bit.platformer.controller;

import java.util.Set;

/**
 * Application: исходящий порт получения действий игрока независимо от устройства ввода.
 */
public interface ActionSource {

    Set<PlayerAction> getPressedActions();
}
