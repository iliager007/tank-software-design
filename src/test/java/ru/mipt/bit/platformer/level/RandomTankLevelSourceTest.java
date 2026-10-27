package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.WorldSettings;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class RandomTankLevelSourceTest {

    @Test
    public void placesTanksInDistinctFreeCellsAcrossSeedsAndPreservesLevel() throws IOException {
        WorldSettings initial = new WorldSettings(new GridPoint2(1, 1), 0.8f,
                Arrays.asList(new GridPoint2(0, 0), new GridPoint2(3, 2)));
        for (int seed = 0; seed < 100; seed++) {
            LevelDefinition level = new RandomTankLevelSource(() -> new LevelDefinition(7, 5, initial),
                    8, new Random(seed)).load();
            WorldSettings settings = level.getWorldSettings();
            Set<GridPoint2> occupied = new HashSet<>(settings.getObstacleCoordinates());
            occupied.add(settings.getTankCoordinates());

            assertEquals(7, level.getWidth());
            assertEquals(5, level.getHeight());
            assertEquals(initial.getTankCoordinates(), settings.getTankCoordinates());
            assertEquals(initial.getObstacleCoordinates(), settings.getObstacleCoordinates());
            assertEquals(0.8f, settings.getTankMovementDuration(), 0f);
            assertEquals(8, settings.getAiTankCoordinates().size());
            for (GridPoint2 cell : settings.getAiTankCoordinates()) {
                assertTrue(occupied.add(cell));
                assertTrue(cell.x >= 0 && cell.x < 7 && cell.y >= 0 && cell.y < 5);
            }
        }
        assertTrue(initial.getAiTankCoordinates().isEmpty());
    }

    @Test
    public void preservesExistingAiAndProducesReproduciblePositions() throws IOException {
        WorldSettings initial = new WorldSettings(new GridPoint2(0, 0), 0.4f,
                Collections.emptyList(), Collections.singletonList(new GridPoint2(1, 0)));
        LevelSource source = () -> new LevelDefinition(3, 2, initial);

        WorldSettings first = new RandomTankLevelSource(source, 3, new Random(42)).load().getWorldSettings();
        WorldSettings second = new RandomTankLevelSource(source, 3, new Random(42)).load().getWorldSettings();

        assertEquals(first.getAiTankCoordinates(), second.getAiTankCoordinates());
        assertEquals(4, new HashSet<>(first.getAiTankCoordinates()).size());
        assertEquals(new GridPoint2(1, 0), first.getAiTankCoordinates().get(0));
        assertFalse(first.getAiTankCoordinates().contains(new GridPoint2(0, 0)));
        assertEquals(1, initial.getAiTankCoordinates().size());
    }

    @Test
    public void usesAvailableCellsWhenRequestedCountExceedsCapacity() throws IOException {
        LevelSource source = () -> new TextLevelParser(0.4f).parse(Arrays.asList("X_T", "___"));

        WorldSettings settings = new RandomTankLevelSource(source, Integer.MAX_VALUE, new Random(1))
                .load().getWorldSettings();

        assertEquals(4, new HashSet<>(settings.getAiTankCoordinates()).size());
        assertFalse(settings.getAiTankCoordinates().contains(new GridPoint2(0, 1)));
        assertFalse(settings.getAiTankCoordinates().contains(new GridPoint2(2, 1)));
    }

    @Test
    public void supportsSingleCellFullLevelAndZeroAdditionalTanks() throws IOException {
        LevelSource singleCell = () -> new TextLevelParser(0.4f).parse(Collections.singletonList("X"));
        assertTrue(new RandomTankLevelSource(singleCell, 3, new Random(1)).load()
                .getWorldSettings().getAiTankCoordinates().isEmpty());
        LevelSource full = () -> new TextLevelParser(0.4f).parse(Collections.singletonList("XT"));
        assertTrue(new RandomTankLevelSource(full, 3, new Random(1)).load()
                .getWorldSettings().getAiTankCoordinates().isEmpty());
        LevelSource empty = () -> new TextLevelParser(0.4f).parse(Collections.singletonList("X__"));
        assertTrue(new RandomTankLevelSource(empty, 0, new Random(1)).load()
                .getWorldSettings().getAiTankCoordinates().isEmpty());
    }

    @Test
    public void rejectsInvalidCountNullDependenciesAndTooLargeLevels() {
        LevelSource source = () -> new LevelDefinition(10, 8, WorldSettings.standard());
        assertThrows(IllegalArgumentException.class, () -> new RandomTankLevelSource(source, -1, new Random()));
        assertThrows(NullPointerException.class, () -> new RandomTankLevelSource(null, 1, new Random()));
        assertThrows(NullPointerException.class, () -> new RandomTankLevelSource(source, 1, null));
        LevelSource huge = () -> new LevelDefinition(Integer.MAX_VALUE, 2, WorldSettings.standard());
        assertThrows(IllegalArgumentException.class,
                () -> new RandomTankLevelSource(huge, 1, new Random()).load());
    }

    @Test
    public void propagatesSourceReadFailure() {
        IOException failure = new IOException("Cannot read test level");
        LevelSource source = () -> { throw failure; };

        assertSame(failure, assertThrows(IOException.class,
                () -> new RandomTankLevelSource(source, 3, new Random()).load()));
    }
}
