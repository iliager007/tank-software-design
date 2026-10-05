package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.WorldView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameRenderer {

    private final Batch batch;
    private final MapRenderer levelRenderer;
    private final Disposable levelResources;
    private final List<WorldRenderLayer> layers;

    public GameRenderer(Batch batch, MapRenderer levelRenderer, Disposable levelResources,
                        List<? extends WorldRenderLayer> layers) {
        this.batch = batch;
        this.levelRenderer = levelRenderer;
        this.levelResources = levelResources;
        this.layers = Collections.unmodifiableList(new ArrayList<>(layers));
    }

    public void render(WorldView world) {
        levelRenderer.render();

        batch.begin();
        for (WorldRenderLayer layer : layers) {
            layer.render(batch, world);
        }
        batch.end();
    }

    public void dispose() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            layers.get(i).dispose();
        }
        levelResources.dispose();
        batch.dispose();
    }
}
