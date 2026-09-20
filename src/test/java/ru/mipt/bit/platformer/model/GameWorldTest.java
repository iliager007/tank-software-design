package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class GameWorldTest {

    @Test
    public void startsWithTankAndTreeAtExpectedCells() {
        GameWorld world = new GameWorld();

        assertEquals(new GridPoint2(1, 1), world.getTank().getCurrentCoordinates());
        assertEquals(1, world.getObstacles().size());
        assertEquals(new GridPoint2(1, 3), world.getObstacles().get(0).getCoordinates());
        assertThrows(UnsupportedOperationException.class, () -> world.getObstacles().clear());
    }

    @Test
    public void appliesControllerDecisionAndAdvancesMovement() {
        GameWorld world = new GameWorld();

        world.update((tank, view) -> Optional.of(Direction.UP), 0.2f);

        assertEquals(new GridPoint2(1, 1), world.getTank().getCurrentCoordinates());
        assertEquals(new GridPoint2(1, 2), world.getTank().getDestinationCoordinates());
        assertEquals(0.5f, world.getTank().getMovementProgress(), 0.0001f);

        world.update((tank, view) -> Optional.empty(), 0.2f);
        assertEquals(new GridPoint2(1, 2), world.getTank().getCurrentCoordinates());
    }

    @Test
    public void preventsMovingIntoTree() {
        GameWorld world = new GameWorld();
        world.update((tank, view) -> Optional.of(Direction.UP), 0.4f);

        world.update((tank, view) -> Optional.of(Direction.UP), 0.1f);

        assertEquals(new GridPoint2(1, 2), world.getTank().getCurrentCoordinates());
        assertEquals(new GridPoint2(1, 2), world.getTank().getDestinationCoordinates());
        assertEquals(1f, world.getTank().getMovementProgress(), 0f);
    }
}
