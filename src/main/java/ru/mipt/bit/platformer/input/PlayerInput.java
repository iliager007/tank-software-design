package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;
import java.util.EnumSet;

public final class PlayerInput {

    private static final KeyBinding[] KEY_BINDINGS = {
            new KeyBinding(PlayerAction.MOVE_UP, Input.Keys.UP, Input.Keys.W),
            new KeyBinding(PlayerAction.MOVE_LEFT, Input.Keys.LEFT, Input.Keys.A),
            new KeyBinding(PlayerAction.MOVE_DOWN, Input.Keys.DOWN, Input.Keys.S),
            new KeyBinding(PlayerAction.MOVE_RIGHT, Input.Keys.RIGHT, Input.Keys.D)
    };

    private final Input input;

    public PlayerInput(Input input) {
        this.input = input;
    }

    public EnumSet<PlayerAction> getPressedActions() {
        EnumSet<PlayerAction> actions = EnumSet.noneOf(PlayerAction.class);
        for (KeyBinding keyBinding : KEY_BINDINGS) {
            if (keyBinding.isPressed(input)) {
                actions.add(keyBinding.getAction());
            }
        }
        return actions;
    }

    private static final class KeyBinding {

        private final PlayerAction action;
        private final int[] keys;

        private KeyBinding(PlayerAction action, int... keys) {
            this.action = action;
            this.keys = keys;
        }

        private boolean isPressed(Input input) {
            for (int key : keys) {
                if (input.isKeyPressed(key)) {
                    return true;
                }
            }
            return false;
        }

        private PlayerAction getAction() {
            return action;
        }
    }
}
