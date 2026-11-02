package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public final class BulletTextureFactory {

    public Texture create() {
        Pixmap pixmap = new Pixmap(20, 10, Pixmap.Format.RGBA8888);
        try {
            pixmap.setColor(Color.ORANGE);
            pixmap.fillRectangle(0, 2, 14, 6);
            pixmap.fillCircle(14, 5, 4);
            pixmap.setColor(Color.YELLOW);
            pixmap.fillRectangle(3, 3, 11, 4);
            return new Texture(pixmap);
        } finally {
            pixmap.dispose();
        }
    }
}
