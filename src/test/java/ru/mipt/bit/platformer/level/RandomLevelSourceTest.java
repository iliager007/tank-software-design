package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.WorldSettings;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class RandomLevelSourceTest {

    @Test
    public void placesDistinctObjectsInsideLevelAcrossSeeds() {
        for (int seed = 0; seed < 100; seed++) {
            LevelDefinition level = new RandomLevelSource(10, 6, 20, 0.8f, new Random(seed)).load();
            WorldSettings settings = level.getWorldSettings();
            Set<GridPoint2> occupied = new HashSet<>(settings.getObstacleCoordinates());

            assertEquals(10, level.getWidth());
            assertEquals(6, level.getHeight());
            assertEquals(20, occupied.size());
            assertTrue(occupied.add(settings.getTankCoordinates()));
            assertEquals(0.8f, settings.getTankMovementDuration(), 0f);
            for (GridPoint2 cell : occupied) {
                assertTrue(cell.x >= 0 && cell.x < 10 && cell.y >= 0 && cell.y < 6);
            }
        }
    }

    @Test
    public void supportsEmptyAndAlmostFullLevels() {
        LevelDefinition singleCell = new RandomLevelSource(1, 1, 0, 0.4f, new Random(1)).load();
        assertEquals(new GridPoint2(0, 0), singleCell.getWorldSettings().getTankCoordinates());
        assertTrue(singleCell.getWorldSettings().getObstacleCoordinates().isEmpty());

        LevelDefinition full = new RandomLevelSource(2, 2, 3, 0.4f, new Random(1)).load();
        Set<GridPoint2> cells = new HashSet<>(full.getWorldSettings().getObstacleCoordinates());
        assertTrue(cells.add(full.getWorldSettings().getTankCoordinates()));
        assertEquals(4, cells.size());
    }

    @Test
    public void injectedRandomMakesGenerationReproducible() {
        WorldSettings first = new RandomLevelSource(10, 8, 20, 0.4f, new Random(42)).load().getWorldSettings();
        WorldSettings second = new RandomLevelSource(10, 8, 20, 0.4f, new Random(42)).load().getWorldSettings();

        assertEquals(first.getTankCoordinates(), second.getTankCoordinates());
        assertEquals(first.getObstacleCoordinates(), second.getObstacleCoordinates());
    }

    @Test
    public void rejectsInvalidDimensionsAndObstacleCounts() {
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(0, 2, 0, 0.4f, new Random()));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(2, -1, 0, 0.4f, new Random()));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(2, 2, -1, 0.4f, new Random()));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(2, 2, 4, 0.4f, new Random()));
        assertThrows(IllegalArgumentException.class,
                () -> new RandomLevelSource(Integer.MAX_VALUE, 2, 0, 0.4f, new Random()));
    }
}
