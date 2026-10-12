package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class GroundLayerFactoryTest {

    @Test
    public void adaptsGroundDimensionsAndRepeatsTemplateCells() {
        TiledMapTileLayer template = new TiledMapTileLayer(2, 1, 128, 128);
        template.setName("Ground");
        TiledMapTileLayer.Cell first = new TiledMapTileLayer.Cell();
        TiledMapTileLayer.Cell second = new TiledMapTileLayer.Cell();
        template.setCell(0, 0, first);
        template.setCell(1, 0, second);

        TiledMapTileLayer layer = new GroundLayerFactory().create(template, 3, 2);

        assertEquals(3, layer.getWidth());
        assertEquals(2, layer.getHeight());
        assertEquals(128, layer.getTileWidth());
        assertEquals(128, layer.getTileHeight());
        assertEquals("Ground", layer.getName());
        assertSame(first, layer.getCell(0, 0));
        assertSame(second, layer.getCell(1, 1));
        assertSame(first, layer.getCell(2, 1));
        assertEquals(2, template.getWidth());
    }
}
