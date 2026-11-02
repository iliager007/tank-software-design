package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class RenderWorldTest {

    @Test
    public void subscriptionUsesSameEventsForInitialObjectsAndChanges() {
        GameWorld world = new GameWorld();
        RenderWorld graphics = new RenderWorld(world);

        assertEquals(world.getObjects(), graphics.getObjects());
        assertEquals(world.getTanks(), graphics.getTanks());
        assertEquals(world.getObstacles(), graphics.getObstacles());
        world.update((tank, view) -> Optional.of(new ShootTankCommand()), 0f);
        assertEquals(world.getObjects(), graphics.getObjects());
        assertEquals(1, graphics.getBullets().size());
        world.update((tank, view) -> Optional.empty(), 2f);
        assertTrue(graphics.getBullets().isEmpty());
        assertEquals(world.getObjects(), graphics.getObjects());
    }

    @Test
    public void removedTankDisappearsAndUnsubscribedViewReceivesNoEvents() {
        GameWorld world = new GameWorld();
        RenderWorld graphics = new RenderWorld(world);
        GameObject player = (GameObject) world.getTank();

        world.removeObject(player);
        assertTrue(graphics.getTanks().isEmpty());
        assertFalse(graphics.getObjects().contains(player));
        graphics.dispose();
        world.addObject(player);
        assertTrue(graphics.getTanks().isEmpty());
    }

    @Test
    public void duplicateRegistrationAndRemovalSendExactlyOneEvent() {
        GameWorld world = new GameWorld();
        List<String> events = new ArrayList<>();
        WorldListener listener = new WorldListener() {
            @Override
            public void objectAdded(GameObjectView object) {
                events.add("added");
            }

            @Override
            public void objectRemoved(GameObjectView object) {
                events.add("removed");
            }
        };
        world.addListener(listener);
        world.addListener(listener);
        assertEquals(Arrays.asList("added", "added"), events);
        Bullet bullet = new Bullet(new GridPoint2(3, 1), Direction.RIGHT, BulletSettings.standard());
        world.addObject(bullet);
        world.addObject(bullet);
        world.removeObject(bullet);
        world.removeObject(bullet);
        world.removeListener(listener);
        world.addObject(bullet);

        assertEquals(Arrays.asList("added", "added", "added", "removed"), events);
        assertThrows(UnsupportedOperationException.class, () -> world.getObjects().clear());
    }
}
