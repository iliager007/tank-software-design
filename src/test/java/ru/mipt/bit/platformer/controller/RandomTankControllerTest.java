package ru.mipt.bit.platformer.controller;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.CollisionMovementPolicy;
import ru.mipt.bit.platformer.model.Tank;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class RandomTankControllerTest {

    @Test
    public void choosesAllFourDirectionsUsingInjectedRandom() {
        RandomTankController controller = new RandomTankController(new Random(42));
        GridPoint2[] expected = {new GridPoint2(4, 3), new GridPoint2(4, 5),
                new GridPoint2(4, 3), new GridPoint2(4, 5), new GridPoint2(3, 4), new GridPoint2(5, 4)};
        GameWorld world = new GameWorld();
        for (GridPoint2 destination : expected) {
            Tank tank = new Tank(new GridPoint2(4, 4), 0.4f);
            controller.chooseCommand(tank, world).get().execute(tank, new CollisionMovementPolicy(), world);

            assertEquals(destination, tank.getDestinationCoordinates());
            assertEquals(new GridPoint2(4, 4), tank.getCurrentCoordinates());
        }
    }

    @Test
    public void selectingCommandDoesNotStartMovement() {
        GameWorld world = new GameWorld();

        assertTrue(new RandomTankController(new Random(42)).chooseCommand(world.getTank(), world).isPresent());

        assertEquals(new GridPoint2(1, 1), world.getTank().getDestinationCoordinates());
        assertThrows(NullPointerException.class, () -> new RandomTankController(null));
    }
}
