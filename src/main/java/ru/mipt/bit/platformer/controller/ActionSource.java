package ru.mipt.bit.platformer.controller;

import java.util.Set;

public interface ActionSource {

    Set<PlayerAction> getPressedActions();
}
