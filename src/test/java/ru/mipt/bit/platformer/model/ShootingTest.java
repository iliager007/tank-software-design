package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ShootingTest {

    private final TankController idle = (tank, world) -> Optional.empty();
    private final TankController shoot = (tank, world) -> Optional.of(new ShootTankCommand());

    @Test
    public void shotStartsInNextCellAndKeepsDirectionAfterTankTurns() {
        GameWorld world = world(new GridPoint2(1, 1));

        world.update(shoot, 0f);
        BulletView bullet = world.getBullets().get(0);
        assertEquals(new GridPoint2(2, 1), bullet.getCurrentCoordinates());
        assertEquals(Direction.RIGHT, bullet.getDirection());

        world.update(move(Direction.UP), 0.1f);

        assertEquals(new GridPoint2(3, 1), bullet.getCurrentCoordinates());
        assertEquals(Direction.RIGHT, bullet.getDirection());
        assertEquals(90f, world.getTank().getRotation(), 0f);
    }

    @Test
    public void adjacentTankTakesConfiguredDamageImmediatelyAndDiesOnlyBelowZero() {
        GameWorld world = world(new GridPoint2(1, 1), new GridPoint2(2, 1));
        TankView target = world.getTanks().get(1);

        world.update(shoot, 0f);
        assertEquals(50, target.getHealth());
        assertTrue(world.getBullets().isEmpty());

        world.update(idle, 0.4f);
        world.update(shoot, 0f);
        assertEquals(0, target.getHealth());
        assertEquals(2, world.getTanks().size());

        world.update(idle, 0.4f);
        world.update(shoot, 0f);
        assertEquals(-50, target.getHealth());
        assertEquals(Collections.singletonList(world.getTank()), world.getTanks());
    }

    @Test
    public void adjacentTreeAndAllBoundariesConsumeShot() {
        GameWorld tree = new GameWorld(6, 6, new WorldSettings(new GridPoint2(1, 1), 0.4f,
                Collections.singletonList(new GridPoint2(2, 1))), new CollisionMovementPolicy());
        tree.update(shoot, 0f);
        assertTrue(tree.getBullets().isEmpty());
        assertEquals(1, tree.getObstacles().size());

        GridPoint2[] edges = {new GridPoint2(0, 5), new GridPoint2(0, 0),
                new GridPoint2(1, 0), new GridPoint2(5, 1)};
        Direction[] directions = {Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT};
        for (int i = 0; i < edges.length; i++) {
            GameWorld world = world(edges[i]);
            world.update(move(directions[i]), 0f);
            world.update(shoot, 0f);
            assertTrue("Boundary " + directions[i], world.getBullets().isEmpty());
        }
    }

    @Test
    public void largeTickCannotSkipTargetOrLeaveBulletsOutsideWorld() {
        GameWorld world = world(new GridPoint2(0, 1), new GridPoint2(4, 1));
        TankView target = world.getTanks().get(1);

        world.update(shoot, 2f);

        assertEquals(50, target.getHealth());
        assertTrue(world.getBullets().isEmpty());
        GameWorld empty = world(new GridPoint2(0, 1));
        empty.update(shoot, 2f);
        assertTrue(empty.getBullets().isEmpty());
    }

    @Test
    public void opposingBulletsDestroyEachOtherWithoutDamagingTanks() {
        GameWorld world = world(new GridPoint2(0, 1), new GridPoint2(4, 1));
        world.update(idle, turn(Direction.LEFT), 0f);
        world.update(shoot, shoot, 0.3f);

        assertTrue(world.getBullets().isEmpty());
        assertEquals(100, world.getTank().getHealth());
        assertEquals(100, world.getTanks().get(1).getHealth());
    }

    @Test
    public void equallyFastBulletsKeepTheirDistanceInEitherRegistrationOrder() {
        for (boolean reverse : new boolean[]{false, true}) {
            GameWorld world = world(new GridPoint2(0, 0));
            Bullet rear = new Bullet(new GridPoint2(0, 1), Direction.RIGHT, BulletSettings.standard());
            Bullet front = new Bullet(new GridPoint2(1, 1), Direction.RIGHT, BulletSettings.standard());
            world.addObject(reverse ? front : rear);
            world.addObject(reverse ? rear : front);

            world.update(idle, 0.3f);

            assertEquals(2, world.getBullets().size());
            assertEquals(new GridPoint2(3, 1), rear.getCurrentCoordinates());
            assertEquals(new GridPoint2(4, 1), front.getCurrentCoordinates());
        }
    }

    @Test
    public void bulletsCollideWhenTheyExchangeCells() {
        GameWorld world = world(new GridPoint2(0, 0));
        world.addObject(new Bullet(new GridPoint2(1, 1), Direction.RIGHT, BulletSettings.standard()));
        world.addObject(new Bullet(new GridPoint2(2, 1), Direction.LEFT, BulletSettings.standard()));

        world.update(idle, 0.1f);

        assertTrue(world.getBullets().isEmpty());
    }

    @Test
    public void allBulletsArrivingInSameCellDisappear() {
        GameWorld world = world(new GridPoint2(0, 0));
        world.addObject(new Bullet(new GridPoint2(1, 2), Direction.RIGHT, BulletSettings.standard()));
        world.addObject(new Bullet(new GridPoint2(3, 2), Direction.LEFT, BulletSettings.standard()));
        world.addObject(new Bullet(new GridPoint2(2, 1), Direction.UP, BulletSettings.standard()));

        world.update(idle, 0.1f);

        assertTrue(world.getBullets().isEmpty());
    }

    @Test
    public void crossingTrajectoriesAtDifferentTimesDoNotCollide() {
        for (boolean reverse : new boolean[]{false, true}) {
            GameWorld world = world(new GridPoint2(0, 0));
            Bullet horizontal = new Bullet(new GridPoint2(1, 1), Direction.RIGHT, BulletSettings.standard());
            Bullet vertical = new Bullet(new GridPoint2(3, 1), Direction.UP, BulletSettings.standard());
            world.addObject(reverse ? vertical : horizontal);
            world.addObject(reverse ? horizontal : vertical);

            world.update(idle, 0.3f);

            assertEquals(2, world.getBullets().size());
            assertEquals(new GridPoint2(4, 1), horizontal.getCurrentCoordinates());
            assertEquals(new GridPoint2(3, 4), vertical.getCurrentCoordinates());
        }
    }

    @Test
    public void hitChecksBothReservedCellsOfMovingTank() {
        for (int x : new int[]{2, 3}) {
            GameWorld world = world(new GridPoint2(x, 0), new GridPoint2(2, 1));
            world.update(turn(Direction.UP), move(Direction.RIGHT), 0.1f);
            world.update(shoot, 0f);
            assertEquals(50, world.getTanks().get(1).getHealth());
        }
    }

    @Test
    public void allCommandsAreChosenBeforeAnyIsExecutedAndDeadTankCannotExecute() {
        GameWorld world = new GameWorld(6, 6, new WorldSettings(new GridPoint2(1, 1), 0.4f,
                Collections.emptyList(), Collections.singletonList(new GridPoint2(2, 1)),
                10, new BulletSettings(20, 0.1f)), new CollisionMovementPolicy());
        AtomicInteger decisions = new AtomicInteger();
        TankController ai = (tank, view) -> {
            assertEquals(10, tank.getHealth());
            assertTrue(view.getBullets().isEmpty());
            decisions.incrementAndGet();
            return Optional.of(new ShootTankCommand());
        };

        world.update(shoot, ai, 0.1f);

        assertEquals(1, decisions.get());
        assertEquals(1, world.getTanks().size());
        assertTrue(world.getBullets().isEmpty());
    }

    @Test
    public void shootingCadenceDoesNotDependOnFrameRate() {
        GameWorld world = world(new GridPoint2(0, 1));
        world.update(shoot, 0f);
        world.update(shoot, 0f);
        world.update(shoot, 0.1f);
        assertEquals(1, world.getBullets().size());
    }

    @Test
    public void cooldownExpiresAfterSeveralShortTicks() {
        GameWorld world = world(new GridPoint2(0, 1));
        world.update(shoot, 0f);
        for (int i = 0; i < 4; i++) {
            world.update(idle, 0.1f);
        }

        world.update(shoot, 0f);

        assertEquals(2, world.getBullets().size());
    }

    @Test
    public void bulletAdvancesAfterSeveralShortTicks() {
        GameWorld world = world(new GridPoint2(0, 1));
        world.update(shoot, 0f);
        BulletView bullet = world.getBullets().get(0);
        for (int i = 0; i < 10; i++) {
            world.update(idle, 0.01f);
        }

        assertEquals(new GridPoint2(2, 1), bullet.getCurrentCoordinates());
        assertEquals(0f, bullet.getMovementProgress(), 0.00001f);
    }

    @Test
    public void killedPlayerReceivesNoMoreCommandsAndReleasesCell() {
        GameWorld world = new GameWorld(6, 6, new WorldSettings(new GridPoint2(2, 1), 0.4f,
                Collections.emptyList(), Collections.singletonList(new GridPoint2(1, 1)),
                10, new BulletSettings(20, 0.1f)), new CollisionMovementPolicy());
        world.update(idle, shoot, 0f);
        assertEquals(1, world.getTanks().size());

        world.update((tank, view) -> { throw new AssertionError("Dead player controlled"); },
                move(Direction.RIGHT), 0.4f);

        assertEquals(new GridPoint2(2, 1), world.getTanks().get(0).getCurrentCoordinates());
    }

    @Test
    public void invalidBulletDurationsDamageAndTicksCannotEnterSimulation() {
        float[] invalidDurations = {0f, -0.1f, Float.NaN, Float.POSITIVE_INFINITY};
        for (float duration : invalidDurations) {
            assertThrows(IllegalArgumentException.class, () -> new BulletSettings(10, duration));
        }
        assertThrows(IllegalArgumentException.class, () -> new BulletSettings(0, 0.1f));
        assertThrows(IllegalArgumentException.class, () -> new BulletSettings(-1, 0.1f));
        GameWorld world = world(new GridPoint2(0, 0));
        for (float tick : new float[]{-1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> world.update(shoot, tick));
        }
        assertTrue(world.getBullets().isEmpty());
    }

    private GameWorld world(GridPoint2 player, GridPoint2... ai) {
        return new GameWorld(6, 6, new WorldSettings(player, 0.4f,
                Collections.emptyList(), Arrays.asList(ai)), new CollisionMovementPolicy());
    }

    @Test
    public void shooterCanFollowItsBulletWithoutTakingDamage() {
        GameWorld world = world(new GridPoint2(0, 1));
        world.update(shoot, 0f);

        world.update(move(Direction.RIGHT), 0.01f);

        assertEquals(100, world.getTank().getHealth());
        assertEquals(1, world.getBullets().size());
    }

    @Test
    public void aiCanFollowItsBulletWithoutTakingDamage() {
        GameWorld world = world(new GridPoint2(0, 0), new GridPoint2(1, 1));
        world.update(idle, shoot, 0f);

        world.update(idle, move(Direction.RIGHT), 0.01f);

        assertEquals(100, world.getTanks().get(1).getHealth());
        assertEquals(1, world.getBullets().size());
    }

    private TankController move(Direction direction) {
        return (tank, world) -> Optional.of(new MoveTankCommand(direction));
    }

    private TankController turn(Direction direction) {
        return (tank, world) -> Optional.of((receiver, policy, context) ->
                receiver.tryMove(direction, (destination, view) -> false, context));
    }
}
