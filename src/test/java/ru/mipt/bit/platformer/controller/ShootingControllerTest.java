package ru.mipt.bit.platformer.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.input.KeyBinding;
import ru.mipt.bit.platformer.input.PlayerInput;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.CollisionMovementPolicy;
import ru.mipt.bit.platformer.model.WorldSettings;
import ru.mipt.bit.platformer.model.ShootTankCommand;
import ru.mipt.bit.platformer.model.TankCommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;
import static ru.mipt.bit.platformer.input.PressedKeysInput.withKeys;

public class ShootingControllerTest {

    @Test
    public void spaceDuringMovementFiresImmediatelyAndHitsEnemy() {
        GameWorld world = new GameWorld(new WorldSettings(
                new GridPoint2(1, 1), 0.4f, Collections.emptyList(),
                Collections.singletonList(new GridPoint2(3, 1))),
                new CollisionMovementPolicy());
        Set<PlayerAction> actions = EnumSet.of(PlayerAction.MOVE_RIGHT);
        PlayerTankController controller = new PlayerTankController(() -> actions,
                Collections.singletonList(new MovementBinding(PlayerAction.MOVE_RIGHT, Direction.RIGHT)));
        world.update(controller, 0.05f);
        actions.clear();
        actions.add(PlayerAction.SHOOT);

        world.update(controller, 0.01f);

        assertEquals(1, world.getBullets().size());
        assertEquals(100, world.getTank().getHealth());
        assertEquals(new GridPoint2(2, 1), world.getTank().getDestinationCoordinates());
        actions.clear();
        world.update(controller, 0.1f);
        assertEquals(50, world.getTanks().get(1).getHealth());
        assertTrue(world.getBullets().isEmpty());
    }

    @Test
    public void spaceProducesShotWithPriorityOverMovement() {
        GameWorld world = new GameWorld();
        PlayerInput input = new PlayerInput(withKeys(Input.Keys.SPACE, Input.Keys.UP), Arrays.asList(
                new KeyBinding(PlayerAction.SHOOT, Input.Keys.SPACE),
                new KeyBinding(PlayerAction.MOVE_UP, Input.Keys.UP)));
        PlayerTankController controller = new PlayerTankController(input,
                Collections.singletonList(new MovementBinding(PlayerAction.MOVE_UP, Direction.UP)));

        world.update(controller, 0f);

        assertEquals(1, world.getBullets().size());
        assertEquals(new GridPoint2(1, 1), world.getTank().getDestinationCoordinates());
    }

    @Test
    public void randomControllerCanChooseShooting() {
        GameWorld world = new GameWorld();
        RandomTankController controller = new RandomTankController(new Random(3));
        boolean shotChosen = false;
        for (int i = 0; i < 100; i++) {
            TankCommand command = controller.chooseCommand(world.getTank(), world).get();
            shotChosen |= command instanceof ShootTankCommand;
        }

        assertTrue(shotChosen);
        assertTrue(world.getBullets().isEmpty());
    }

    @Test
    public void recommendationAdapterSupportsAllDirectionsAndShooting() {
        Recommendation[] recommendations = {Recommendation.MOVE_UP, Recommendation.MOVE_LEFT,
                Recommendation.MOVE_DOWN, Recommendation.MOVE_RIGHT, Recommendation.SHOOT};
        GridPoint2[] destinations = {new GridPoint2(1, 2), new GridPoint2(0, 1),
                new GridPoint2(1, 0), new GridPoint2(2, 1), new GridPoint2(1, 1)};
        for (int i = 0; i < recommendations.length; i++) {
            GameWorld world = new GameWorld();
            Recommendation recommendation = recommendations[i];
            RecommendationTankController controller = new RecommendationTankController((tank, view) -> {
                assertSame(world.getTank(), tank);
                assertSame(world, view);
                return Optional.of(recommendation);
            });

            world.update(controller, 0f);

            assertEquals(destinations[i], world.getTank().getDestinationCoordinates());
            assertEquals(recommendation == Recommendation.SHOOT ? 1 : 0, world.getBullets().size());
        }
    }

    @Test
    public void recommendationAdapterAllowsNoRecommendation() {
        GameWorld world = new GameWorld();
        RecommendationTankController controller = new RecommendationTankController((tank, view) -> Optional.empty());

        assertFalse(controller.chooseCommand(world.getTank(), world).isPresent());
        assertThrows(NullPointerException.class, () -> new RecommendationTankController(null));
    }
}
