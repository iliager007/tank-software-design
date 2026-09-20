package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Obstacle;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;
import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

final class ObstacleRenderer {

    private final TiledMapTileLayer groundLayer;
    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;

    ObstacleRenderer(TiledMapTileLayer groundLayer) {
        this.groundLayer = groundLayer;
        texture = new Texture("images/greenTree.png");
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
    }

    void render(Batch batch, Obstacle obstacle) {
        moveRectangleAtTileCenter(groundLayer, rectangle, obstacle.getCoordinates());
        drawTextureRegionUnscaled(batch, graphics, rectangle, 0f);
    }

    void dispose() {
        texture.dispose();
    }
}
