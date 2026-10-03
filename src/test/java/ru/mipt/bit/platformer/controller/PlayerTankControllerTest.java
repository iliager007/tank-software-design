package ru.mipt.bit.platformer.controller;

import com.badlogic.gdx.Input;
import org.junit.Test;
import ru.mipt.bit.platformer.input.KeyBinding;
import ru.mipt.bit.platformer.input.PlayerInput;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameWorld;

import java.util.Arrays;
import java.util.EnumSet;
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

    @Test
    public void acceptsAnotherPriorityAndActionSource() {
        GameWorld world = new GameWorld();
        PlayerTankController controller = new PlayerTankController(
                () -> EnumSet.of(PlayerAction.MOVE_UP, PlayerAction.MOVE_RIGHT),
                Arrays.asList(
                        new MovementBinding(PlayerAction.MOVE_RIGHT, Direction.RIGHT),
                        new MovementBinding(PlayerAction.MOVE_UP, Direction.UP)
                )
        );

        assertEquals(Optional.of(Direction.RIGHT), controller.chooseDirection(world.getTank(), world));
    }

    private Optional<Direction> chooseDirection(int... pressedKeys) {
        GameWorld world = new GameWorld();
        return new PlayerTankController(new PlayerInput(withKeys(pressedKeys), Arrays.asList(
                new KeyBinding(PlayerAction.MOVE_UP, Input.Keys.UP, Input.Keys.W),
                new KeyBinding(PlayerAction.MOVE_LEFT, Input.Keys.LEFT, Input.Keys.A),
                new KeyBinding(PlayerAction.MOVE_DOWN, Input.Keys.DOWN, Input.Keys.S),
                new KeyBinding(PlayerAction.MOVE_RIGHT, Input.Keys.RIGHT, Input.Keys.D)
        )), Arrays.asList(
                new MovementBinding(PlayerAction.MOVE_UP, Direction.UP),
                new MovementBinding(PlayerAction.MOVE_LEFT, Direction.LEFT),
                new MovementBinding(PlayerAction.MOVE_DOWN, Direction.DOWN),
                new MovementBinding(PlayerAction.MOVE_RIGHT, Direction.RIGHT)
        ))
                .chooseDirection(world.getTank(), world);
    }
}
