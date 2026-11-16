package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.render.HealthBarRenderer;

/**
 * Infrastructure: UI-команда графического адаптера, управляющая HealthBarRenderer.
 */
public final class ToggleHealthBarsCommand implements Runnable {

    private final HealthBarRenderer renderer;

    public ToggleHealthBarsCommand(HealthBarRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void run() {
        renderer.toggleVisibility();
    }
}
