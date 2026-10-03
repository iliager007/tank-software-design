package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.ObstacleView;
import ru.mipt.bit.platformer.model.WorldView;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;
import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

public final class ObstacleRenderer implements WorldRenderLayer {

    private final TiledMapTileLayer groundLayer;
    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;

    public ObstacleRenderer(TiledMapTileLayer groundLayer, Texture texture) {
        this.groundLayer = groundLayer;
        this.texture = texture;
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
    }

    @Override
    public void render(Batch batch, WorldView world) {
        for (ObstacleView obstacle : world.getObstacles()) {
            moveRectangleAtTileCenter(groundLayer, rectangle, obstacle.getCoordinates());
            drawTextureRegionUnscaled(batch, graphics, rectangle, 0f);
        }
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
