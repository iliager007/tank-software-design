package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TankStateTest {

    private final TankState fastState = new TankState() {
        @Override
        public float getMovementSpeedMultiplier() {
            return 2f;
        }

        @Override
        public float getFireRateMultiplier() {
            return 2f;
        }
    };

    @Test
    public void movementSpeedUsesInclusiveMediumThresholdsAndInitialHealth() {
        int[][] healthCases = {
                {100, 100}, {100, 71}, {100, 70}, {100, 15}, {100, 14}, {100, 0},
                {200, 141}, {200, 140}, {200, 30}, {200, 29}, {3, 2}, {7, 1},
                {Integer.MAX_VALUE, 1503238553}, {Integer.MAX_VALUE, 1503238552},
                {Integer.MAX_VALUE, 322122548}, {Integer.MAX_VALUE, 322122547}
        };
        float[] expectedProgress = {
                1f, 1f, 0.5f, 0.5f, 1f / 3f, 1f / 3f,
                1f, 0.5f, 0.5f, 1f / 3f, 0.5f, 1f / 3f,
                1f, 0.5f, 0.5f, 1f / 3f
        };

        for (int i = 0; i < healthCases.length; i++) {
            GameWorld world = world(healthCases[i][0]);
            Tank tank = (Tank) world.getTank();
            damage(tank, healthCases[i][0] - healthCases[i][1], world);
            tank.tryMove(Direction.RIGHT, new CollisionMovementPolicy(), world);

            tank.update(0.4f);

            assertEquals("Health " + healthCases[i][1] + "/" + healthCases[i][0],
                    expectedProgress[i], tank.getMovementProgress(), 0.00001f);
        }
    }

    @Test
    public void heavyDamagePreventsShootingButMediumDamageAllowsIt() {
        int[] remainingHealth = {100, 71, 70, 15, 14, 0};
        int[] expectedBullets = {1, 1, 1, 1, 0, 0};
        for (int i = 0; i < remainingHealth.length; i++) {
            GameWorld world = world(100);
            Tank tank = (Tank) world.getTank();
            damage(tank, 100 - remainingHealth[i], world);

            tank.shoot(world);

            assertEquals("Health " + remainingHealth[i], expectedBullets[i], world.getBullets().size());
        }
    }

    @Test
    public void damageChangesSpeedDuringMovementWithoutResettingProgress() {
        GameWorld world = world(100);
        Tank tank = (Tank) world.getTank();
        tank.tryMove(Direction.RIGHT, new CollisionMovementPolicy(), world);
        tank.update(0.1f);

        damage(tank, 30, world);
        assertEquals(0.25f, tank.getMovementProgress(), 0.00001f);
        tank.update(0.2f);
        assertEquals(0.5f, tank.getMovementProgress(), 0.00001f);

        damage(tank, 56, world);
        tank.update(0.3f);
        assertEquals(0.75f, tank.getMovementProgress(), 0.00001f);
        assertEquals(new GridPoint2(1, 1), tank.getCurrentCoordinates());

        tank.update(0.3f);
        assertEquals(new GridPoint2(2, 1), tank.getCurrentCoordinates());
        assertEquals(1f, tank.getMovementProgress(), 0f);
    }

    @Test
    public void mediumDamageKeepsOriginalShootingInterval() {
        GameWorld world = world(100);
        Tank tank = (Tank) world.getTank();
        damage(tank, 30, world);
        tank.shoot(world);
        tank.update(0.2f);
        tank.shoot(world);
        assertEquals(1, world.getBullets().size());
        world.removeObject((Bullet) world.getBullets().get(0));

        tank.update(0.2f);
        tank.shoot(world);

        assertEquals(1, world.getBullets().size());
    }

    @Test
    public void damageImmediatelyPreventsShootingDuringMovement() {
        GameWorld world = world(100);
        Tank tank = (Tank) world.getTank();
        tank.tryMove(Direction.UP, new CollisionMovementPolicy(), world);
        tank.update(0.1f);
        damage(tank, 86, world);

        tank.shoot(world);
        tank.update(2f);
        tank.shoot(world);

        assertTrue(world.getBullets().isEmpty());
    }

    @Test
    public void zeroInitialHealthUsesHeavyStateAndStaysAlive() {
        GameWorld world = world(0);
        Tank tank = (Tank) world.getTank();
        tank.tryMove(Direction.RIGHT, new CollisionMovementPolicy(), world);

        tank.update(0.4f);
        tank.shoot(world);

        assertEquals(1f / 3f, tank.getMovementProgress(), 0.00001f);
        assertTrue(world.contains(tank));
        assertTrue(world.getBullets().isEmpty());
    }

    @Test
    public void damageStatesApplyToAiTanksThroughWorldUpdate() {
        GameWorld world = new GameWorld(6, 6, new WorldSettings(new GridPoint2(0, 0), 0.4f,
                Collections.emptyList(), Collections.singletonList(new GridPoint2(1, 1))),
                new CollisionMovementPolicy());
        Tank ai = (Tank) world.getTanks().get(1);
        damage(ai, 86, world);

        world.update((tank, view) -> Optional.empty(),
                (tank, view) -> Optional.of(new MoveTankCommand(Direction.UP)), 0.4f);

        assertEquals(1f / 3f, ai.getMovementProgress(), 0.00001f);
        world.update((tank, view) -> Optional.empty(),
                (tank, view) -> Optional.of(new ShootTankCommand()), 1f);
        world.update((tank, view) -> Optional.empty(),
                (tank, view) -> Optional.of(new ShootTankCommand()), 0f);
        assertTrue(world.getBullets().isEmpty());
    }

    @Test
    public void lethalHitStillRemovesTankOnlyBelowZero() {
        GameWorld world = world(100);
        Tank tank = (Tank) world.getTank();
        damage(tank, 100, world);
        assertTrue(world.contains(tank));

        damage(tank, 1, world);

        assertFalse(world.contains(tank));
        tank.shoot(world);
        assertTrue(world.getBullets().isEmpty());
    }

    @Test
    public void customStateCanDoubleMovementSpeedAndFireRate() {
        GameWorld world = world(100);
        Tank tank = (Tank) world.getTank();
        tank.changeState(fastState);
        tank.tryMove(Direction.UP, new CollisionMovementPolicy(), world);
        tank.shoot(world);
        world.removeObject((Bullet) world.getBullets().get(0));

        tank.update(0.1f);
        assertEquals(0.5f, tank.getMovementProgress(), 0.00001f);
        tank.shoot(world);
        assertTrue(world.getBullets().isEmpty());

        tank.update(0.1f);
        tank.shoot(world);

        assertEquals(new GridPoint2(1, 2), tank.getCurrentCoordinates());
        assertEquals(1, world.getBullets().size());
    }

    @Test
    public void changingStateAppliesNewRateToRemainingCooldown() {
        GameWorld world = world(100);
        Tank tank = (Tank) world.getTank();
        tank.shoot(world);
        world.removeObject((Bullet) world.getBullets().get(0));
        tank.update(0.1f);

        tank.changeState(fastState);
        tank.update(0.14f);
        tank.shoot(world);
        assertTrue(world.getBullets().isEmpty());

        tank.update(0.01f);
        tank.shoot(world);

        assertEquals(1, world.getBullets().size());
    }

    @Test
    public void customStateControlsItsTransitionOnDamage() {
        GameWorld world = world(100);
        Tank tank = (Tank) world.getTank();
        tank.changeState(new TankState() {
            @Override
            public float getMovementSpeedMultiplier() {
                return 2f;
            }

            @Override
            public TankState onHealthChanged(int health, int maxHealth) {
                return TankDamageState.fromHealth(health, maxHealth);
            }
        });
        tank.tryMove(Direction.RIGHT, new CollisionMovementPolicy(), world);
        tank.update(0.1f);
        damage(tank, 30, world);

        tank.update(0.2f);

        assertEquals(0.75f, tank.getMovementProgress(), 0.00001f);
    }

    private GameWorld world(int health) {
        return new GameWorld(6, 6, new WorldSettings(new GridPoint2(1, 1), 0.4f,
                Collections.emptyList(), Collections.emptyList(), health, BulletSettings.standard()),
                new CollisionMovementPolicy());
    }

    private void damage(Tank tank, int damage, WorldContext world) {
        if (damage > 0) {
            tank.hit(new Bullet(tank.getCurrentCoordinates(), Direction.LEFT, new BulletSettings(damage, 0.1f)),
                    world);
        }
    }
}
