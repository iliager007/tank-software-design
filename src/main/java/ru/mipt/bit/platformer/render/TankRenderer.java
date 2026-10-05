package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.TankView;
import ru.mipt.bit.platformer.model.WorldView;
import ru.mipt.bit.platformer.util.TileMovement;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

public final class TankRenderer implements WorldRenderLayer {

    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;
    private final TileMovement tileMovement;

    public TankRenderer(TiledMapTileLayer groundLayer, Texture texture, Interpolation interpolation) {
        this.texture = texture;
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
        tileMovement = new TileMovement(groundLayer, interpolation);
    }

    @Override
    public void render(Batch batch, WorldView world) {
        TankView tank = world.getTank();
        tileMovement.moveRectangleBetweenTileCenters(
                rectangle,
                tank.getCurrentCoordinates(),
                tank.getDestinationCoordinates(),
                tank.getMovementProgress()
        );
        drawTextureRegionUnscaled(batch, graphics, rectangle, tank.getRotation());
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
