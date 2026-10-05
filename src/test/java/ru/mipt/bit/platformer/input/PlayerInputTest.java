package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;
import org.junit.Test;
import ru.mipt.bit.platformer.controller.PlayerAction;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static ru.mipt.bit.platformer.input.PressedKeysInput.withKeys;

public class PlayerInputTest {

    @Test
    public void mapsArrowAndWasdKeysToActions() {
        int[] keys = {
                Input.Keys.UP, Input.Keys.W,
                Input.Keys.LEFT, Input.Keys.A,
                Input.Keys.DOWN, Input.Keys.S,
                Input.Keys.RIGHT, Input.Keys.D
        };
        PlayerAction[] actions = {
                PlayerAction.MOVE_UP, PlayerAction.MOVE_UP,
                PlayerAction.MOVE_LEFT, PlayerAction.MOVE_LEFT,
                PlayerAction.MOVE_DOWN, PlayerAction.MOVE_DOWN,
                PlayerAction.MOVE_RIGHT, PlayerAction.MOVE_RIGHT
        };

        for (int i = 0; i < keys.length; i++) {
            assertEquals("Key " + keys[i], EnumSet.of(actions[i]), input(keys[i]).getPressedActions());
        }
    }

    @Test
    public void returnsIndependentActionsForSimultaneousKeys() {
        assertEquals(
                EnumSet.of(PlayerAction.MOVE_UP, PlayerAction.MOVE_RIGHT),
                input(Input.Keys.W, Input.Keys.D).getPressedActions()
        );
    }

    @Test
    public void returnsEmptySetWhenNothingIsPressed() {
        assertEquals(EnumSet.noneOf(PlayerAction.class), input().getPressedActions());
    }

    @Test
    public void acceptsDifferentKeyBindings() {
        PlayerInput input = new PlayerInput(withKeys(Input.Keys.SPACE),
                Collections.singletonList(new KeyBinding(PlayerAction.MOVE_UP, Input.Keys.SPACE)));

        assertEquals(EnumSet.of(PlayerAction.MOVE_UP), input.getPressedActions());
    }

    private PlayerInput input(int... pressedKeys) {
        return new PlayerInput(withKeys(pressedKeys), Arrays.asList(
                new KeyBinding(PlayerAction.MOVE_UP, Input.Keys.UP, Input.Keys.W),
                new KeyBinding(PlayerAction.MOVE_LEFT, Input.Keys.LEFT, Input.Keys.A),
                new KeyBinding(PlayerAction.MOVE_DOWN, Input.Keys.DOWN, Input.Keys.S),
                new KeyBinding(PlayerAction.MOVE_RIGHT, Input.Keys.RIGHT, Input.Keys.D)
        ));
    }
}
