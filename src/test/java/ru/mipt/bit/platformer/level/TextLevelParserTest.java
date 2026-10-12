package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TextLevelParserTest {

    private final TextLevelParser parser = new TextLevelParser(0.4f);

    @Test
    public void readsExampleWithFirstRowAtTheTop() {
        LevelDefinition level = parser.parse(Arrays.asList(
                "___T__T___", "__TT__TTTT", "_________T",
                "TTTT__T__T", "_____X____", "__________"));

        assertEquals(10, level.getWidth());
        assertEquals(6, level.getHeight());
        assertEquals(new GridPoint2(5, 1), level.getWorldSettings().getTankCoordinates());
        assertEquals(15, level.getWorldSettings().getObstacleCoordinates().size());
        assertEquals(new GridPoint2(3, 5), level.getWorldSettings().getObstacleCoordinates().get(0));
        assertEquals(new GridPoint2(9, 2), level.getWorldSettings().getObstacleCoordinates().get(14));
        assertEquals(0.4f, level.getWorldSettings().getTankMovementDuration(), 0f);
    }

    @Test
    public void supportsSinglePlayerCell() {
        LevelDefinition level = parser.parse(Collections.singletonList("X"));

        assertEquals(new GridPoint2(0, 0), level.getWorldSettings().getTankCoordinates());
        assertEquals(Collections.emptyList(), level.getWorldSettings().getObstacleCoordinates());
    }

    @Test
    public void rejectsEmptyOrNonRectangularLevels() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(Collections.emptyList()));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(Collections.singletonList("")));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(Arrays.asList("X_", "_")));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(Arrays.asList("X_", "")));
    }

    @Test
    public void requiresExactlyOnePlayerStart() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(Collections.singletonList("_T")));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(Collections.singletonList("XX")));
    }

    @Test
    public void rejectsUnknownSymbolsWithLocation() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> parser.parse(Arrays.asList("X_", "_?")));

        assertEquals("Unknown level symbol at row 2, column 2: ?", error.getMessage());
        assertThrows(IllegalArgumentException.class, () -> parser.parse(Collections.singletonList("X ")));
    }
}
