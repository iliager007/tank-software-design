package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapRenderer;
import org.junit.Test;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.WorldView;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameRendererTest {

    @Test
    public void rendersConfiguredLayersInOrderAndDisposesOwnedResources() {
        List<String> events = new ArrayList<>();
        Batch batch = recordingProxy(Batch.class, events, "batch.");
        MapRenderer mapRenderer = recordingProxy(MapRenderer.class, events, "map.");
        GameRenderer renderer = new GameRenderer(batch, mapRenderer,
                () -> events.add("level.dispose"), Arrays.asList(
                        layer("tank", events), layer("obstacle", events), layer("extra", events)));

        renderer.render(new GameWorld());
        renderer.dispose();

        assertEquals(Arrays.asList("map.render", "batch.begin", "tank.render", "obstacle.render",
                "extra.render", "batch.end", "extra.dispose", "obstacle.dispose", "tank.dispose",
                "level.dispose", "batch.dispose"), events);
    }

    @Test
    public void recordingProxySupportsObjectMethodsWithoutRecordingEvents() {
        List<String> events = new ArrayList<>();
        Batch batch = recordingProxy(Batch.class, events, "batch.");
        Batch other = recordingProxy(Batch.class, events, "batch.");

        Object[] candidates = {batch, other, null};
        boolean[] expectedEquality = {true, false, false};
        for (int i = 0; i < candidates.length; i++) {
            assertEquals("Equality for candidate " + i,
                    expectedEquality[i], batch.equals(candidates[i]));
        }
        assertEquals(System.identityHashCode(batch), batch.hashCode());
        assertEquals("RecordingProxy<Batch>", batch.toString());
        assertTrue(events.isEmpty());
    }

    private WorldRenderLayer layer(String name, List<String> events) {
        return new WorldRenderLayer() {
            @Override
            public void render(Batch batch, WorldView world) {
                events.add(name + ".render");
            }

            @Override
            public void dispose() {
                events.add(name + ".dispose");
            }
        };
    }

    @SuppressWarnings("unchecked")
    private <T> T recordingProxy(Class<T> type, List<String> events, String prefix) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        switch (method.getName()) {
                            case "equals":
                                return proxy == args[0];
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "toString":
                                return "RecordingProxy<" + type.getSimpleName() + ">";
                            default:
                                throw new UnsupportedOperationException(method.toString());
                        }
                    }
                    events.add(prefix + method.getName());
                    return null;
                });
    }
}
