package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public final class HealthBarTextureFactory {

    public Texture create() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        try {
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            return new Texture(pixmap);
        } finally {
            pixmap.dispose();
        }
    }
}
