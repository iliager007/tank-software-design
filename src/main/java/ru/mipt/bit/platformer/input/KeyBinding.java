package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.controller.PlayerAction;

public final class KeyBinding {

    private final PlayerAction action;
    private final int[] keys;

    public KeyBinding(PlayerAction action, int... keys) {
        this.action = action;
        this.keys = keys.clone();
    }

    public boolean isPressed(Input input) {
        for (int key : keys) {
            if (input.isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }

    public PlayerAction getAction() {
        return action;
    }
}
