package ru.mipt.bit.platformer.model;

import ru.mipt.bit.platformer.controller.TankController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameWorld implements WorldView {

    private final Tank tank;
    private final List<Obstacle> obstacles;

    public GameWorld(Tank tank, List<Obstacle> obstacles) {
        this.tank = tank;
        this.obstacles = Collections.unmodifiableList(new ArrayList<>(obstacles));
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
