package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MoveTankCommand;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.CollisionMovementPolicy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class FileLevelSourceTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void readsFileAndCreatesWorldWithExistingCollisionRules() throws IOException {
        Path file = folder.newFile("level.txt").toPath();
        Files.write(file, "_T_\r\n_X_\r\n___\r\n".getBytes(StandardCharsets.UTF_8));
        LevelSource source = new FileLevelSource(file, new TextLevelParser(0.4f));
        LevelDefinition level = source.load();
        GameWorld world = new GameWorld(level.getWidth(), level.getHeight(), level.getWorldSettings(),
                new CollisionMovementPolicy());

        assertEquals(new GridPoint2(1, 1), world.getTank().getCurrentCoordinates());
        assertEquals(new GridPoint2(1, 2), world.getObstacles().get(0).getCoordinates());
        world.update((tank, view) -> Optional.of(new MoveTankCommand(Direction.UP)), 0.4f);
        assertEquals(new GridPoint2(1, 1), world.getTank().getCurrentCoordinates());
        world.update((tank, view) -> Optional.of(new MoveTankCommand(Direction.RIGHT)), 0.4f);
        assertEquals(new GridPoint2(2, 1), world.getTank().getCurrentCoordinates());
    }

    @Test
    public void reportsMissingFile() {
        LevelSource source = new FileLevelSource(folder.getRoot().toPath().resolve("missing.txt"),
                new TextLevelParser(0.4f));

        assertThrows(IOException.class, source::load);
    }

    @Test
    public void reportsMalformedFile() throws IOException {
        Path file = folder.newFile("invalid.txt").toPath();
        Files.write(file, "TT".getBytes(StandardCharsets.UTF_8));
        LevelSource source = new FileLevelSource(file, new TextLevelParser(0.4f));

        assertThrows(IllegalArgumentException.class, source::load);
    }
}
