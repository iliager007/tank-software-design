package ru.mipt.bit.platformer.model;

import java.util.List;

/**
 * Domain: входящий порт чтения состояния мира для контроллеров и представления.
 */
public interface WorldView {

    int getWidth();

    int getHeight();

    TankView getTank();

    List<TankView> getTanks();

    List<ObstacleView> getObstacles();

    List<BulletView> getBullets();

    List<GameObjectView> getObjects();
}
