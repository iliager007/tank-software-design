package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
        assertObstaclesCannotBeModified(world);
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

    @Test
    public void createsObjectsFromSettingsAndCopiesTheirPositions() {
        GridPoint2 tankPosition = new GridPoint2(4, 4);
        List<GridPoint2> obstaclePositions = new ArrayList<>(Arrays.asList(
                new GridPoint2(5, 4), new GridPoint2(6, 4)));
        WorldSettings settings = new WorldSettings(tankPosition, 0.8f, obstaclePositions);
        tankPosition.set(9, 9);
        obstaclePositions.get(0).set(8, 8);
        obstaclePositions.clear();

        GameWorld world = new GameWorld(settings, new ObstacleMovementPolicy());
        assertEquals(new GridPoint2(4, 4), world.getTank().getCurrentCoordinates());
        assertEquals(2, world.getObstacles().size());
        assertEquals(new GridPoint2(5, 4), world.getObstacles().get(0).getCoordinates());
        assertObstaclesCannotBeModified(world);

        world.update((tank, view) -> Optional.of(Direction.UP), 0.2f);
        assertEquals(0.25f, world.getTank().getMovementProgress(), 0.0001f);
    }

    @Test
    public void supportsEmptyObstaclesAndDifferentMovementPolicies() {
        WorldSettings settings = new WorldSettings(new GridPoint2(1, 1), 0.4f,
                Collections.emptyList());
        GameWorld blocked = new GameWorld(settings, (destination, world) -> false);
        blocked.update((tank, view) -> Optional.of(Direction.UP), 0.1f);
        assertEquals(new GridPoint2(1, 1), blocked.getTank().getDestinationCoordinates());
        assertEquals(90f, blocked.getTank().getRotation(), 0f);

        GameWorld open = new GameWorld(settings, (destination, world) -> true);
        open.update((tank, view) -> Optional.of(Direction.UP), 0.1f);
        assertEquals(new GridPoint2(1, 2), open.getTank().getDestinationCoordinates());
    }

    // Намеренная попытка изменения проверяет защиту списка препятствий.
    @SuppressWarnings("ImmutableCollection")
    private void assertObstaclesCannotBeModified(WorldView world) {
        assertThrows(UnsupportedOperationException.class, () -> world.getObstacles().clear());
    }

    @Test
    public void movementPolicyCannotChangeRequestedDestination() {
        GameWorld world = new GameWorld(WorldSettings.standard(), (destination, view) -> {
            destination.set(9, 9);
            return true;
        });

        world.update((tank, view) -> Optional.of(Direction.RIGHT), 0.1f);

        assertEquals(new GridPoint2(2, 1), world.getTank().getDestinationCoordinates());
    }
}
