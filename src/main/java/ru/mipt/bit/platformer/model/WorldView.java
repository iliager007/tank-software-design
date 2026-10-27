package ru.mipt.bit.platformer.model;

import java.util.List;

public interface WorldView {

    int getWidth();

    int getHeight();

    TankView getTank();

    List<TankView> getTanks();

    List<ObstacleView> getObstacles();
}
