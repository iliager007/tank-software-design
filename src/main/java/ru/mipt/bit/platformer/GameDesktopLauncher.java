package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.controller.MovementBinding;
import ru.mipt.bit.platformer.controller.PlayerAction;
import ru.mipt.bit.platformer.controller.PlayerTankController;
import ru.mipt.bit.platformer.input.KeyBinding;
import ru.mipt.bit.platformer.input.PlayerInput;
import ru.mipt.bit.platformer.level.FileLevelSource;
import ru.mipt.bit.platformer.level.LevelDefinition;
import ru.mipt.bit.platformer.level.LevelSource;
import ru.mipt.bit.platformer.level.RandomLevelSource;
import ru.mipt.bit.platformer.level.TextLevelParser;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.ObstacleMovementPolicy;
import ru.mipt.bit.platformer.model.TankController;
import ru.mipt.bit.platformer.model.WorldSettings;
import ru.mipt.bit.platformer.render.GameRenderer;
import ru.mipt.bit.platformer.render.GroundLayerFactory;
import ru.mipt.bit.platformer.render.ObstacleRenderer;
import ru.mipt.bit.platformer.render.TankRenderer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;

public class GameDesktopLauncher implements ApplicationListener {

    private final LevelSource levelSource;
    private GameWorld gameWorld;
    private TankController tankController;
    private GameRenderer gameRenderer;

    public GameDesktopLauncher() {
        this(() -> new LevelDefinition(10, 8, WorldSettings.standard()));
    }

    public GameDesktopLauncher(LevelSource levelSource) {
        this.levelSource = Objects.requireNonNull(levelSource, "levelSource");
    }

    @Override
    public void create() {
        LevelDefinition definition;
        try {
            definition = levelSource.load();
        } catch (IOException exception) {
            throw new UncheckedIOException("Cannot read level", exception);
        }
        gameWorld = new GameWorld(definition.getWorldSettings(), new ObstacleMovementPolicy());
        PlayerInput playerInput = new PlayerInput(Gdx.input, Arrays.asList(
                new KeyBinding(PlayerAction.MOVE_UP, Input.Keys.UP, Input.Keys.W),
                new KeyBinding(PlayerAction.MOVE_LEFT, Input.Keys.LEFT, Input.Keys.A),
                new KeyBinding(PlayerAction.MOVE_DOWN, Input.Keys.DOWN, Input.Keys.S),
                new KeyBinding(PlayerAction.MOVE_RIGHT, Input.Keys.RIGHT, Input.Keys.D)
        ));
        tankController = new PlayerTankController(playerInput, Arrays.asList(
                new MovementBinding(PlayerAction.MOVE_UP, Direction.UP),
                new MovementBinding(PlayerAction.MOVE_LEFT, Direction.LEFT),
                new MovementBinding(PlayerAction.MOVE_DOWN, Direction.DOWN),
                new MovementBinding(PlayerAction.MOVE_RIGHT, Direction.RIGHT)
        ));

        Batch batch = new SpriteBatch();
        TiledMap level = new TmxMapLoader().load("level.tmx");
        TiledMapTileLayer template = getSingleLayer(level);
        TiledMapTileLayer groundLayer = new GroundLayerFactory().create(template,
                definition.getWidth(), definition.getHeight());
        level.getLayers().remove(template);
        level.getLayers().add(groundLayer);
        level.getProperties().put("width", definition.getWidth());
        level.getProperties().put("height", definition.getHeight());
        MapRenderer levelRenderer = createSingleLayerMapRenderer(level, batch);
        Disposable levelRendererResource = (Disposable) levelRenderer;
        Disposable levelResources = () -> {
            levelRendererResource.dispose();
            level.dispose();
        };
        gameRenderer = new GameRenderer(batch, levelRenderer, levelResources, Arrays.asList(
                new TankRenderer(groundLayer, new Texture("images/tank_blue.png"), Interpolation.smooth),
                new ObstacleRenderer(groundLayer, new Texture("images/greenTree.png"))
        ));
    }

    @Override
    public void render() {
        // clear the screen
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        // get time passed since the last render
        float deltaTime = Gdx.graphics.getDeltaTime();

        gameWorld.update(tankController, deltaTime);
        gameRenderer.render(gameWorld);
    }

    @Override
    public void resize(int width, int height) {
        // do not react to window resizing
    }

    @Override
    public void pause() {
        // game doesn't get paused
    }

    @Override
    public void resume() {
        // game doesn't get paused
    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
    }

    public static void main(String[] args) {
        GameDesktopLauncher launcher;
        if (args.length == 0) {
            launcher = new GameDesktopLauncher();
        } else if (args.length == 2 && "file".equals(args[0])) {
            launcher = new GameDesktopLauncher(new FileLevelSource(Paths.get(args[1]), new TextLevelParser(0.4f)));
        } else if ((args.length == 1 || args.length == 2) && "random".equals(args[0])) {
            int obstacleCount = args.length == 2 ? Integer.parseInt(args[1]) : 20;
            launcher = new GameDesktopLauncher(new RandomLevelSource(10, 8, obstacleCount, 0.4f, new Random()));
        } else {
            throw new IllegalArgumentException("Usage: [random [obstacleCount] | file <path>]");
        }
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(launcher, config);
    }
}
