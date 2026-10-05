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
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.TankController;
import ru.mipt.bit.platformer.render.GameRenderer;
import ru.mipt.bit.platformer.render.ObstacleRenderer;
import ru.mipt.bit.platformer.render.TankRenderer;

import java.util.Arrays;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;

public class GameDesktopLauncher implements ApplicationListener {

    private GameWorld gameWorld;
    private TankController tankController;
    private GameRenderer gameRenderer;

    @Override
    public void create() {
        gameWorld = new GameWorld();
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
        TiledMapTileLayer groundLayer = getSingleLayer(level);
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
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
