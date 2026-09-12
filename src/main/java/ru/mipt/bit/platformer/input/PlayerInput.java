package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.model.Direction;

import java.util.Optional;

public final class PlayerInput {

    private static final KeyBinding[] KEY_BINDINGS = {
            new KeyBinding(Direction.UP, Input.Keys.UP, Input.Keys.W),
            new KeyBinding(Direction.LEFT, Input.Keys.LEFT, Input.Keys.A),
            new KeyBinding(Direction.DOWN, Input.Keys.DOWN, Input.Keys.S),
            new KeyBinding(Direction.RIGHT, Input.Keys.RIGHT, Input.Keys.D)
    };

    private final Input input;

    public PlayerInput(Input input) {
        this.input = input;
    }

    public Optional<Direction> getRequestedDirection() {
        for (KeyBinding keyBinding : KEY_BINDINGS) {
            if (keyBinding.isPressed(input)) {
                return Optional.of(keyBinding.getDirection());
            }
        }
        return Optional.empty();
    }

    private static final class KeyBinding {

        private final Direction direction;
        private final int primaryKey;
        private final int alternativeKey;

        private KeyBinding(Direction direction, int primaryKey, int alternativeKey) {
            this.direction = direction;
            this.primaryKey = primaryKey;
            this.alternativeKey = alternativeKey;
        }

        private boolean isPressed(Input input) {
            return input.isKeyPressed(primaryKey) || input.isKeyPressed(alternativeKey);
        }

        private Direction getDirection() {
            return direction;
        }
    }
}
