package ru.mipt.bit.platformer.controller;

import com.badlogic.gdx.Input;
import org.junit.Test;
import ru.mipt.bit.platformer.input.PlayerInput;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameWorld;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static ru.mipt.bit.platformer.input.PressedKeysInput.withKeys;

public class PlayerTankControllerTest {

    @Test
    public void keepsMovementPriorityWhenSeveralDirectionsArePressed() {
        assertEquals(Optional.of(Direction.UP), chooseDirection(Input.Keys.UP, Input.Keys.LEFT));
        assertEquals(Optional.of(Direction.LEFT), chooseDirection(Input.Keys.LEFT, Input.Keys.DOWN));
        assertEquals(Optional.of(Direction.DOWN), chooseDirection(Input.Keys.DOWN, Input.Keys.RIGHT));
        assertEquals(Optional.of(Direction.RIGHT), chooseDirection(Input.Keys.D));
    }

    @Test
    public void returnsNoDirectionWithoutInput() {
        assertEquals(Optional.empty(), chooseDirection());
    }

    private Optional<Direction> chooseDirection(int... pressedKeys) {
        GameWorld world = new GameWorld();
        return new PlayerTankController(new PlayerInput(withKeys(pressedKeys)))
                .chooseDirection(world.getTank(), world);
    }
}
