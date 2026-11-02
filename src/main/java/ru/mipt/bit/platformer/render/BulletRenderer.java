package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.BulletView;
import ru.mipt.bit.platformer.model.WorldView;
import ru.mipt.bit.platformer.util.TileMovement;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

public final class BulletRenderer implements WorldRenderLayer {

    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;
    private final TileMovement movement;

    public BulletRenderer(TiledMapTileLayer groundLayer, Texture texture) {
        this.texture = texture;
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
        movement = new TileMovement(groundLayer, Interpolation.linear);
    }

    @Override
    public void render(Batch batch, WorldView world) {
        for (BulletView bullet : world.getBullets()) {
            movement.moveRectangleBetweenTileCenters(rectangle, bullet.getCurrentCoordinates(),
                    bullet.getDestinationCoordinates(), bullet.getMovementProgress());
            drawTextureRegionUnscaled(batch, graphics, rectangle, bullet.getDirection().getRotation());
        }
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
