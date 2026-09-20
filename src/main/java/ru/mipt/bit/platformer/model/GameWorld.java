package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.controller.TankController;

import java.util.Collections;
import java.util.List;

public final class GameWorld implements WorldView {

    private static final float TANK_MOVEMENT_SPEED = 0.4f;

    private final Tank tank;
    private final List<Obstacle> obstacles;

    public GameWorld() {
        this.tank = new Tank(new GridPoint2(1, 1), TANK_MOVEMENT_SPEED);
        this.obstacles = Collections.singletonList(new Obstacle(new GridPoint2(1, 3)));
    }

    public void update(TankController tankController, float deltaTime) {
        tankController.chooseDirection(tank, this)
                .ifPresent(direction -> tank.tryMove(direction, obstacles));
        tank.update(deltaTime);
    }

    public Tank getTank() {
        return tank;
    }

    @Override
    public List<Obstacle> getObstacles() {
        return obstacles;
    }
}
