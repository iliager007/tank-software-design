package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.controller.RandomTankController;
import ru.mipt.bit.platformer.level.LevelDefinition;
import ru.mipt.bit.platformer.level.RandomLevelSource;
import ru.mipt.bit.platformer.level.RandomTankLevelSource;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class GameSimulationTest {

    @Test
    public void repeatedRandomCommandsKeepAllReservationsDistinctInsideLevel() throws IOException {
        for (int seed = 0; seed < 20; seed++) {
            LevelDefinition level = new RandomTankLevelSource(
                    new RandomLevelSource(7, 5, 6, 0.4f, new Random(seed)),
                    8, new Random(seed + 100)).load();
            GameWorld world = new GameWorld(level.getWidth(), level.getHeight(), level.getWorldSettings(),
                    new CollisionMovementPolicy());
            TankController player = new RandomTankController(new Random(seed + 200));
            TankController ai = new RandomTankController(new Random(seed + 300));
            for (int frame = 0; frame < 200; frame++) {
                world.update(player, ai, 0.1f);
                Set<GridPoint2> occupied = new HashSet<>();
                for (ObstacleView obstacle : world.getObstacles()) {
                    occupied.add(obstacle.getCoordinates());
                }
                for (TankView tank : world.getTanks()) {
                    Set<GridPoint2> reserved = new HashSet<>();
                    reserved.add(tank.getCurrentCoordinates());
                    reserved.add(tank.getDestinationCoordinates());
                    for (GridPoint2 cell : reserved) {
                        assertTrue("Overlap at seed " + seed + ", frame " + frame, occupied.add(cell));
                        assertTrue(cell.x >= 0 && cell.x < 7 && cell.y >= 0 && cell.y < 5);
                    }
                }
            }
        }
    }
}
