package ru.mipt.bit.platformer.model;

import java.util.List;

public interface WorldView {

    TankView getTank();

    List<ObstacleView> getObstacles();
}
