package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.Obstacle;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;

public final class GameRenderer {

    private final Batch batch;
    private final TiledMap level;
    private final MapRenderer levelRenderer;
    private final TankRenderer tankRenderer;
    private final ObstacleRenderer obstacleRenderer;

    public GameRenderer() {
        batch = new SpriteBatch();
        level = new TmxMapLoader().load("level.tmx");
        TiledMapTileLayer groundLayer = getSingleLayer(level);
        levelRenderer = createSingleLayerMapRenderer(level, batch);
        tankRenderer = new TankRenderer(groundLayer);
        obstacleRenderer = new ObstacleRenderer(groundLayer);
    }

    public void render(GameWorld gameWorld) {
        levelRenderer.render();

        batch.begin();
        tankRenderer.render(batch, gameWorld.getTank());
        for (Obstacle obstacle : gameWorld.getObstacles()) {
            obstacleRenderer.render(batch, obstacle);
        }
        batch.end();
    }

    public void dispose() {
        obstacleRenderer.dispose();
        tankRenderer.dispose();
        level.dispose();
        batch.dispose();
    }
}
