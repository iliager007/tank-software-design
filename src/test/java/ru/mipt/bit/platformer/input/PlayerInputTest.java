package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;
import org.junit.Test;

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
            assertEquals("Key " + keys[i], EnumSet.of(actions[i]), new PlayerInput(withKeys(keys[i])).getPressedActions());
        }
    }

    @Test
    public void returnsIndependentActionsForSimultaneousKeys() {
        assertEquals(
                EnumSet.of(PlayerAction.MOVE_UP, PlayerAction.MOVE_RIGHT),
                new PlayerInput(withKeys(Input.Keys.W, Input.Keys.D)).getPressedActions()
        );
    }

    @Test
    public void returnsEmptySetWhenNothingIsPressed() {
        assertEquals(EnumSet.noneOf(PlayerAction.class), new PlayerInput(withKeys()).getPressedActions());
    }
}
