package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.bit.platformer.controller.ToggleHealthBarsCommand;
import ru.mipt.bit.platformer.model.BulletSettings;
import ru.mipt.bit.platformer.model.CollisionMovementPolicy;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.MoveTankCommand;
import ru.mipt.bit.platformer.model.ShootTankCommand;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.WorldSettings;
import ru.mipt.bit.platformer.model.WorldView;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HealthBarRendererTest {

    private final List<String> events = new ArrayList<>();
    private final List<float[]> rectangles = new ArrayList<>();
    private final Color color = new Color(Color.CYAN);
    private GL20 previousGl;
    private Graphics previousGraphics;
    private HealthBarRenderer renderer;
    private Batch batch;

    @Before
    public void setUp() {
        previousGl = Gdx.gl;
        previousGraphics = Gdx.graphics;
        Gdx.graphics = (Graphics) Proxy.newProxyInstance(Graphics.class.getClassLoader(),
                new Class<?>[]{Graphics.class}, (proxy, method, args) -> {
                    if (method.getName().equals("supportsExtension")) {
                        return false;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        Gdx.gl = (GL20) Proxy.newProxyInstance(GL20.class.getClassLoader(), new Class<?>[]{GL20.class},
                (proxy, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        switch (method.getName()) {
                            case "equals": return proxy == args[0];
                            case "hashCode": return System.identityHashCode(proxy);
                            case "toString": return "TestGL20";
                            default: throw new UnsupportedOperationException(method.getName());
                        }
                    }
                    if (method.getName().equals("glGenTexture")) {
                        return 1;
                    }
                    if (method.getName().equals("glDeleteTexture")) {
                        events.add("texture.dispose");
                    }
                    return null;
                });
        TextureData data = (TextureData) Proxy.newProxyInstance(TextureData.class.getClassLoader(),
                new Class<?>[]{TextureData.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getType": return TextureData.TextureDataType.Custom;
                        case "isPrepared": return true;
                        case "isManaged": return false;
                        case "getWidth":
                        case "getHeight": return 1;
                        case "consumeCustomData": return null;
                        default: throw new UnsupportedOperationException(method.getName());
                    }
                });
        WorldRenderLayer delegate = new WorldRenderLayer() {
            @Override
            public void render(Batch batch, WorldView world) {
                events.add("tank.render");
            }

            @Override
            public void dispose() {
                events.add("tank.dispose");
            }
        };
        renderer = new HealthBarRenderer(delegate, new TiledMapTileLayer(6, 6, 128, 128),
                new Texture(data), Interpolation.smooth);
        batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(), new Class<?>[]{Batch.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getPackedColor": return color.toFloatBits();
                        case "setPackedColor":
                            Color.abgr8888ToColor(color, (Float) args[0]);
                            return null;
                        case "setColor":
                            color.set((Color) args[0]);
                            return null;
                        case "draw":
                            assertEquals("tank.render", events.get(events.size() - 1));
                            rectangles.add(new float[]{(Float) args[1], (Float) args[2],
                                    (Float) args[3], (Float) args[4]});
                            return null;
                        default: throw new UnsupportedOperationException(method.getName());
                    }
                });
    }

    @After
    public void tearDown() {
        try {
            if (renderer != null) {
                renderer.dispose();
            }
        } finally {
            Gdx.gl = previousGl;
            Gdx.graphics = previousGraphics;
        }
    }

    @Test
    public void graphicsProxySupportsObjectMethods() {
        assertTrue(Gdx.gl.equals(Gdx.gl));
        assertFalse(Gdx.gl.equals(null));
        assertFalse(Gdx.gl.equals(new Object()));
        assertEquals(System.identityHashCode(Gdx.gl), Gdx.gl.hashCode());
        assertEquals("TestGL20", Gdx.gl.toString());
        assertTrue(events.isEmpty());
    }

    @Test
    public void commandTogglesBarsForAllTanksAndAlwaysRendersDelegate() {
        GameWorld world = world(100);
        Runnable toggle = new ToggleHealthBarsCommand(renderer);

        renderer.render(batch, world);
        assertTrue(rectangles.isEmpty());
        toggle.run();
        renderer.render(batch, world);
        assertEquals(4, rectangles.size());
        assertEquals(64f, rectangles.get(1)[2], 0f);
        assertEquals(64f, rectangles.get(3)[2], 0f);
        assertEquals(Color.CYAN.toFloatBits(), color.toFloatBits(), 0f);
        rectangles.clear();
        toggle.run();
        renderer.render(batch, world);
        assertTrue(rectangles.isEmpty());
        assertEquals(Arrays.asList("tank.render", "tank.render", "tank.render"), events);
    }

    @Test
    public void damageUsesInitialHealthAsMaximum() {
        GameWorld world = world(80);
        world.update((tank, view) -> Optional.of(new ShootTankCommand()), 0f);

        renderer.toggleVisibility();
        renderer.render(batch, world);

        assertEquals(80, world.getTanks().get(1).getMaxHealth());
        assertEquals(60, world.getTanks().get(1).getHealth());
        assertEquals(64f, rectangles.get(1)[2], 0f);
        assertEquals(48f, rectangles.get(3)[2], 0f);
    }

    @Test
    public void barsFollowTankInterpolationWhileMoving() {
        GameWorld world = world(100);
        world.update((tank, view) -> Optional.of(new MoveTankCommand(Direction.UP)), 0.1f);

        renderer.toggleVisibility();
        renderer.render(batch, world);

        assertEquals(160f, rectangles.get(1)[0], 0.001f);
        assertEquals(257f, rectangles.get(1)[1], 0.001f);
        assertEquals(288f, rectangles.get(3)[0], 0.001f);
        assertEquals(237f, rectangles.get(3)[1], 0.001f);
    }

    @Test
    public void zeroInitialHealthProducesAnEmptyFiniteBar() {
        renderer.toggleVisibility();
        renderer.render(batch, world(0));

        for (float[] rectangle : rectangles) {
            for (float coordinate : rectangle) {
                assertTrue(Float.isFinite(coordinate));
            }
            if (rectangle[3] == 6f) {
                assertEquals(0f, rectangle[2], 0f);
            }
        }
        assertTrue(rectangles.size() >= 2);
    }

    @Test
    public void subscriptionAddsNewBarsAndRemovesDestroyedOnes() {
        GameWorld world = world(10);
        RenderWorld view = new RenderWorld(world);
        try {
            renderer.toggleVisibility();
            renderer.render(batch, view);
            assertEquals(4, rectangles.size());
            world.update((tank, context) -> Optional.of(new ShootTankCommand()), 0f);
            rectangles.clear();
            renderer.render(batch, view);
            assertEquals(2, rectangles.size());
            world.addObject(new Tank(new GridPoint2(4, 4), 0.4f));
            rectangles.clear();
            renderer.render(batch, view);
            assertEquals(4, rectangles.size());
        } finally {
            view.dispose();
        }
    }

    @Test
    public void disposeReleasesTextureAndDecoratedLayer() {
        renderer.dispose();
        renderer = null;

        assertEquals(Arrays.asList("texture.dispose", "tank.dispose"), events);
    }

    private GameWorld world(int health) {
        return new GameWorld(6, 6, new WorldSettings(new GridPoint2(1, 1), 0.4f,
                Collections.singletonList(new GridPoint2(1, 3)),
                Collections.singletonList(new GridPoint2(2, 1)), health,
                new BulletSettings(20, 0.1f)), new CollisionMovementPolicy());
    }

}
