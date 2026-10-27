package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.controller.RandomTankController;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

public class MultiTankWorldTest {

    private final TankController idle = (tank, world) -> Optional.empty();

    @Test
    public void rejectsOverlappingAndOutOfBoundsInitialPositions() {
        assertThrows(IllegalArgumentException.class,
                () -> world(new GridPoint2(0, 0), new GridPoint2(0, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> world(new GridPoint2(0, 0), new GridPoint2(1, 0), new GridPoint2(1, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> world(new GridPoint2(0, 0), new GridPoint2(6, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> world(new GridPoint2(-1, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> new GameWorld(2, 2, new WorldSettings(new GridPoint2(0, 0), 0.4f,
                        Collections.singletonList(new GridPoint2(1, 1)),
                        Collections.singletonList(new GridPoint2(1, 1))), new CollisionMovementPolicy()));
    }

    @Test
    public void assignsKeyboardStrategyOnlyToPlayerAndAiStrategyToOtherTanks() {
        GameWorld world = world(new GridPoint2(1, 1), new GridPoint2(4, 4));

        world.update(move(Direction.RIGHT), move(Direction.UP), 0.2f);

        assertSame(world.getTank(), world.getTanks().get(0));
        assertEquals(new GridPoint2(2, 1), world.getTank().getDestinationCoordinates());
        assertEquals(new GridPoint2(4, 5), world.getTanks().get(1).getDestinationCoordinates());
        assertEquals(0.5f, world.getTanks().get(1).getMovementProgress(), 0.0001f);
        assertThrows(UnsupportedOperationException.class, () -> world.getTanks().clear());
    }

    @Test
    public void aiMovesWithoutPlayerInput() {
        GameWorld world = world(new GridPoint2(1, 1), new GridPoint2(4, 4));

        world.update(idle, new RandomTankController(new Random(42)), 0.4f);

        assertEquals(new GridPoint2(1, 1), world.getTank().getCurrentCoordinates());
        assertEquals(new GridPoint2(4, 3), world.getTanks().get(1).getCurrentCoordinates());
    }

    @Test
    public void stationaryTanksBlockEachOtherAndCannotSwapCells() {
        GameWorld world = world(new GridPoint2(1, 1), new GridPoint2(2, 1));

        world.update(move(Direction.RIGHT), idle, 0.4f);
        assertEquals(new GridPoint2(1, 1), world.getTank().getCurrentCoordinates());
        world.update(idle, move(Direction.LEFT), 0.4f);
        assertEquals(new GridPoint2(2, 1), world.getTanks().get(1).getCurrentCoordinates());
        world.update(move(Direction.RIGHT), move(Direction.LEFT), 0.4f);

        assertEquals(new GridPoint2(1, 1), world.getTank().getCurrentCoordinates());
        assertEquals(new GridPoint2(2, 1), world.getTanks().get(1).getCurrentCoordinates());
    }

    @Test
    public void onlyOneTankCanReserveSharedDestinationInSameUpdate() {
        GameWorld world = world(new GridPoint2(0, 0), new GridPoint2(2, 0));

        world.update(move(Direction.RIGHT), move(Direction.LEFT), 0.2f);

        assertEquals(new GridPoint2(1, 0), world.getTank().getDestinationCoordinates());
        assertEquals(new GridPoint2(2, 0), world.getTanks().get(1).getDestinationCoordinates());
    }

    @Test
    public void movingAiReservesBothCellsUntilCompletion() {
        GameWorld world = world(new GridPoint2(0, 1), new GridPoint2(1, 1), new GridPoint2(2, 2));
        TankController ai = (tank, view) -> Optional.of(new MoveTankCommand(
                tank.getCurrentCoordinates().x == 1 ? Direction.RIGHT : Direction.DOWN));

        world.update(move(Direction.RIGHT), ai, 0.2f);
        world.update(move(Direction.RIGHT), ai, 0.2f);

        assertEquals(new GridPoint2(0, 1), world.getTank().getDestinationCoordinates());
        assertEquals(new GridPoint2(2, 1), world.getTanks().get(1).getCurrentCoordinates());
        assertEquals(new GridPoint2(2, 2), world.getTanks().get(2).getDestinationCoordinates());

        world.update(move(Direction.RIGHT), ai, 0f);

        assertEquals(new GridPoint2(1, 1), world.getTank().getDestinationCoordinates());
        assertEquals(new GridPoint2(2, 2), world.getTanks().get(2).getDestinationCoordinates());
    }

    @Test
    public void movingPlayerReservesBothCellsEvenOnCompletionFrame() {
        GameWorld world = world(new GridPoint2(1, 1), new GridPoint2(0, 1), new GridPoint2(2, 2));
        TankController ai = (tank, view) -> Optional.of(new MoveTankCommand(
                tank.getCurrentCoordinates().x == 0 ? Direction.RIGHT : Direction.DOWN));

        world.update(move(Direction.RIGHT), ai, 0.2f);
        world.update(idle, ai, 0.2f);

        assertEquals(new GridPoint2(2, 1), world.getTank().getCurrentCoordinates());
        assertEquals(new GridPoint2(0, 1), world.getTanks().get(1).getDestinationCoordinates());
        assertEquals(new GridPoint2(2, 2), world.getTanks().get(2).getDestinationCoordinates());

        world.update(idle, ai, 0f);
        assertEquals(new GridPoint2(1, 1), world.getTanks().get(1).getDestinationCoordinates());
        assertEquals(new GridPoint2(2, 2), world.getTanks().get(2).getDestinationCoordinates());
    }

    @Test
    public void doesNotAskStrategiesForCommandsInMiddleOfStep() {
        GameWorld world = world(new GridPoint2(1, 1), new GridPoint2(4, 4));
        RandomTankController ai = new RandomTankController(new Random(42));
        world.update(move(Direction.RIGHT), ai, 0.1f);
        TankController failIfCalled = (tank, view) -> {
            throw new AssertionError("Strategy called during movement");
        };

        world.update(failIfCalled, failIfCalled, 0.1f);
        world.update(failIfCalled, failIfCalled, 0.2f);
        world.update(idle, ai, 0f);

        assertEquals(new GridPoint2(4, 4), world.getTanks().get(1).getDestinationCoordinates());
        assertEquals(new GridPoint2(2, 1), world.getTank().getCurrentCoordinates());
    }

    @Test
    public void aiUsesSameTreeAndBoundaryRulesAndRetriesAfterBlockedCommand() {
        GameWorld world = new GameWorld(3, 2, new WorldSettings(new GridPoint2(0, 0), 0.4f,
                Collections.singletonList(new GridPoint2(2, 1)),
                Collections.singletonList(new GridPoint2(2, 0))), new CollisionMovementPolicy());

        world.update(idle, move(Direction.RIGHT), 0.4f);
        world.update(idle, move(Direction.UP), 0.4f);
        assertEquals(new GridPoint2(2, 0), world.getTanks().get(1).getCurrentCoordinates());
        assertEquals(90f, world.getTanks().get(1).getRotation(), 0f);

        world.update(idle, move(Direction.LEFT), 0.4f);
        assertEquals(new GridPoint2(1, 0), world.getTanks().get(1).getCurrentCoordinates());
    }

    private GameWorld world(GridPoint2 player, GridPoint2... aiTanks) {
        return new GameWorld(6, 6, new WorldSettings(player, 0.4f,
                Collections.emptyList(), Arrays.asList(aiTanks)), new CollisionMovementPolicy());
    }

    private TankController move(Direction direction) {
        return (tank, world) -> Optional.of(new MoveTankCommand(direction));
    }
}
