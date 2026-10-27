package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.mipt.bit.platformer.level.LevelDefinition;
import ru.mipt.bit.platformer.model.CollisionMovementPolicy;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.MoveTankCommand;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class GameDesktopLauncherTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void defaultLevelAddsAiAndPreservesOriginalPlayerAndTree() throws IOException {
        LevelDefinition level = GameDesktopLauncher.createLevelSource().load();

        assertEquals(new GridPoint2(1, 1), level.getWorldSettings().getTankCoordinates());
        assertEquals(new GridPoint2(1, 3), level.getWorldSettings().getObstacleCoordinates().get(0));
        assertEquals(3, level.getWorldSettings().getAiTankCoordinates().size());
    }

    @Test
    public void randomArgumentsConfigureObstacleAndAiCounts() throws IOException {
        String[][] arguments = {{"random"}, {"random", "30"}, {"random", "20", "5"},
                {"random", "0", "0"}, {"random", "79", "3"}};
        int[] expectedObstacles = {20, 30, 20, 0, 79};
        int[] expectedAi = {3, 3, 5, 0, 0};
        for (int i = 0; i < arguments.length; i++) {
            LevelDefinition level = GameDesktopLauncher.createLevelSource(arguments[i]).load();

            assertEquals(10, level.getWidth());
            assertEquals(8, level.getHeight());
            assertEquals(expectedObstacles[i], level.getWorldSettings().getObstacleCoordinates().size());
            assertEquals(expectedAi[i], level.getWorldSettings().getAiTankCoordinates().size());
        }
    }

    @Test
    public void fileArgumentsAddAiAndKeepActualDimensionsForMovement() throws IOException {
        Path file = folder.newFile("level.txt").toPath();
        Files.write(file, "___\nX_T\n".getBytes(StandardCharsets.UTF_8));
        LevelDefinition level = GameDesktopLauncher.createLevelSource("file", file.toString(), "1").load();
        GameWorld world = new GameWorld(level.getWidth(), level.getHeight(), level.getWorldSettings(),
                new CollisionMovementPolicy());

        world.update((tank, view) -> Optional.of(new MoveTankCommand(Direction.LEFT)), 0.4f);

        assertEquals(3, world.getWidth());
        assertEquals(2, world.getHeight());
        assertEquals(2, world.getTanks().size());
        assertEquals(new GridPoint2(0, 0), world.getTank().getCurrentCoordinates());
        assertEquals(3, GameDesktopLauncher.createLevelSource("file", file.toString()).load()
                .getWorldSettings().getAiTankCoordinates().size());
    }

    @Test
    public void rejectsInvalidArgumentsAndCounts() {
        String[][] invalid = {{"file"}, {"unknown"}, {"random", "0", "-1"}, {"random", "-1"},
                {"random", "80"}, {"random", "abc"}, {"random", "0", "abc"},
                {"random", "0", "1", "extra"}, {"file", "level.txt", "-1"}};
        for (String[] arguments : invalid) {
            assertThrows(IllegalArgumentException.class, () -> GameDesktopLauncher.createLevelSource(arguments));
        }
    }
}
