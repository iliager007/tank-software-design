package ru.mipt.bit.platformer.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.input.KeyBinding;
import ru.mipt.bit.platformer.input.PlayerInput;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameWorld;

import java.util.Arrays;
import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static ru.mipt.bit.platformer.input.PressedKeysInput.withKeys;

public class PlayerTankControllerTest {

    @Test
    public void keepsMovementPriorityWhenSeveralDirectionsArePressed() {
        assertEquals(new GridPoint2(1, 2), moveWithKeys(Input.Keys.UP, Input.Keys.LEFT));
        assertEquals(new GridPoint2(0, 1), moveWithKeys(Input.Keys.LEFT, Input.Keys.DOWN));
        assertEquals(new GridPoint2(1, 0), moveWithKeys(Input.Keys.DOWN, Input.Keys.RIGHT));
        assertEquals(new GridPoint2(2, 1), moveWithKeys(Input.Keys.D));
    }

    @Test
    public void returnsNoDirectionWithoutInput() {
        assertEquals(new GridPoint2(1, 1), moveWithKeys());
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

        world.update(controller, 0.4f);

        assertEquals(new GridPoint2(2, 1), world.getTank().getCurrentCoordinates());
    }

    private GridPoint2 moveWithKeys(int... pressedKeys) {
        GameWorld world = new GameWorld();
        PlayerTankController controller = new PlayerTankController(new PlayerInput(withKeys(pressedKeys), Arrays.asList(
                new KeyBinding(PlayerAction.MOVE_UP, Input.Keys.UP, Input.Keys.W),
                new KeyBinding(PlayerAction.MOVE_LEFT, Input.Keys.LEFT, Input.Keys.A),
                new KeyBinding(PlayerAction.MOVE_DOWN, Input.Keys.DOWN, Input.Keys.S),
                new KeyBinding(PlayerAction.MOVE_RIGHT, Input.Keys.RIGHT, Input.Keys.D)
        )), Arrays.asList(
                new MovementBinding(PlayerAction.MOVE_UP, Direction.UP),
                new MovementBinding(PlayerAction.MOVE_LEFT, Direction.LEFT),
                new MovementBinding(PlayerAction.MOVE_DOWN, Direction.DOWN),
                new MovementBinding(PlayerAction.MOVE_RIGHT, Direction.RIGHT)
        ));
        world.update(controller, 0.4f);
        return world.getTank().getCurrentCoordinates();
    }
}
