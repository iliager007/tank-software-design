package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.TankView;
import ru.mipt.bit.platformer.model.WorldView;
import ru.mipt.bit.platformer.util.TileMovement;

import static com.badlogic.gdx.math.MathUtils.clamp;

/**
 * Infrastructure: декоратор графического адаптера, отображающий здоровье танков.
 */
public final class HealthBarRenderer implements WorldRenderLayer {

    private static final float WIDTH = 64f;
    private static final float HEIGHT = 6f;
    private static final float OFFSET = 48f;
    private static final float BORDER = 2f;

    private final WorldRenderLayer delegate;
    private final Texture texture;
    private final TileMovement movement;
    private final Rectangle rectangle = new Rectangle(0f, 0f, WIDTH, HEIGHT);
    private boolean visible;

    public HealthBarRenderer(WorldRenderLayer delegate, TiledMapTileLayer groundLayer,
                             Texture texture, Interpolation interpolation) {
        this.delegate = delegate;
        this.texture = texture;
        movement = new TileMovement(groundLayer, interpolation);
    }

    public void toggleVisibility() {
        visible = !visible;
    }

    @Override
    public void render(Batch batch, WorldView world) {
        delegate.render(batch, world);
        if (!visible) {
            return;
        }
        float originalColor = batch.getPackedColor();
        try {
            for (TankView tank : world.getTanks()) {
                movement.moveRectangleBetweenTileCenters(rectangle, tank.getCurrentCoordinates(),
                        tank.getDestinationCoordinates(), tank.getMovementProgress());
                float x = rectangle.x;
                float y = rectangle.y + OFFSET;
                float fraction = tank.getMaxHealth() > 0
                        ? clamp((float) tank.getHealth() / tank.getMaxHealth(), 0f, 1f) : 0f;
                batch.setColor(Color.DARK_GRAY);
                batch.draw(texture, x - BORDER, y - BORDER, WIDTH + 2f * BORDER, HEIGHT + 2f * BORDER);
                if (fraction > 0f) {
                    batch.setColor(Color.GREEN);
                    batch.draw(texture, x, y, WIDTH * fraction, HEIGHT);
                }
            }
        } finally {
            batch.setPackedColor(originalColor);
        }
    }

    @Override
    public void dispose() {
        texture.dispose();
        delegate.dispose();
    }
}
