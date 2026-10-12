package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public final class GroundLayerFactory {

    public TiledMapTileLayer create(TiledMapTileLayer template, int width, int height) {
        TiledMapTileLayer layer = new TiledMapTileLayer(width, height,
                template.getTileWidth(), template.getTileHeight());
        layer.setName(template.getName());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                layer.setCell(x, y, template.getCell(x % template.getWidth(), y % template.getHeight()));
            }
        }
        return layer;
    }
}
