package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WorldSettingsTest {

    @Test
    public void protectsPlayerAndObstacleCoordinatesReturnedToCaller() {
        WorldSettings settings = WorldSettings.standard();

        settings.getTankCoordinates().set(9, 9);
        settings.getObstacleCoordinates().get(0).set(8, 8);
        settings.getObstacleCoordinates().clear();

        assertEquals(new GridPoint2(1, 1), settings.getTankCoordinates());
        assertEquals(Collections.singletonList(new GridPoint2(1, 3)), settings.getObstacleCoordinates());
    }

    @Test
    public void protectsAiCoordinatesFromChangesToSourceAndReturnedLists() {
        GridPoint2 start = new GridPoint2(2, 3);
        List<GridPoint2> starts = new ArrayList<>(Collections.singletonList(start));
        WorldSettings settings = new WorldSettings(new GridPoint2(0, 0), 0.4f,
                Collections.emptyList(), starts);
        start.set(9, 9);
        starts.clear();
        settings.getAiTankCoordinates().get(0).set(8, 8);
        settings.getAiTankCoordinates().clear();

        assertEquals(Collections.singletonList(new GridPoint2(2, 3)), settings.getAiTankCoordinates());
    }

    @Test
    public void oldSettingsCreateOnlyPlayer() {
        assertTrue(WorldSettings.standard().getAiTankCoordinates().isEmpty());
        assertEquals(1, new GameWorld().getTanks().size());
    }
}
