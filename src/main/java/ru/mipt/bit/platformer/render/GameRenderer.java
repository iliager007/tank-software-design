package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.util.TileMovement;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

public final class GameRenderer {

    private final Batch batch;
    private final TiledMap level;
    private final MapRenderer levelRenderer;
    private final TiledMapTileLayer groundLayer;
    private final TileMovement tileMovement;

    private final Texture tankTexture;
    private final TextureRegion tankGraphics;
    private final Rectangle tankRectangle;

    private final Texture treeTexture;
    private final TextureRegion treeGraphics;
    private final Rectangle treeRectangle;

    public GameRenderer() {
        batch = new SpriteBatch();
        level = new TmxMapLoader().load("level.tmx");
        groundLayer = getSingleLayer(level);
        levelRenderer = createSingleLayerMapRenderer(level, batch);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);

        tankTexture = new Texture("images/tank_blue.png");
        tankGraphics = new TextureRegion(tankTexture);
        tankRectangle = createBoundingRectangle(tankGraphics);

        treeTexture = new Texture("images/greenTree.png");
        treeGraphics = new TextureRegion(treeTexture);
        treeRectangle = createBoundingRectangle(treeGraphics);
    }

    public void render(GameWorld gameWorld) {
        levelRenderer.render();

        batch.begin();
        renderTank(gameWorld.getTank());
        for (Obstacle obstacle : gameWorld.getObstacles()) {
            renderObstacle(obstacle);
        }
        batch.end();
    }

    public void dispose() {
        treeTexture.dispose();
        tankTexture.dispose();
        level.dispose();
        batch.dispose();
    }

    private void renderTank(Tank tank) {
        tileMovement.moveRectangleBetweenTileCenters(
                tankRectangle,
                tank.getCurrentCoordinates(),
                tank.getDestinationCoordinates(),
                tank.getMovementProgress()
        );
        drawTextureRegionUnscaled(batch, tankGraphics, tankRectangle, tank.getRotation());
    }

    private void renderObstacle(Obstacle obstacle) {
        moveRectangleAtTileCenter(groundLayer, treeRectangle, obstacle.getCoordinates());
        drawTextureRegionUnscaled(batch, treeGraphics, treeRectangle, 0f);
    }
}
