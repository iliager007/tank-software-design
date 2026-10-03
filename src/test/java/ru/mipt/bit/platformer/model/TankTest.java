package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TankTest {

    @Test
    public void movesToDestinationWhenStepCompletes() {
        Tank tank = new Tank(new GridPoint2(1, 1), 0.4f);

        move(tank, Direction.RIGHT);
        assertEquals(new GridPoint2(1, 1), tank.getCurrentCoordinates());
        assertEquals(new GridPoint2(2, 1), tank.getDestinationCoordinates());
        assertEquals(0f, tank.getMovementProgress(), 0f);
        assertEquals(0f, tank.getRotation(), 0f);

        tank.update(0.2f);
        assertEquals(0.5f, tank.getMovementProgress(), 0.0001f);
        assertEquals(new GridPoint2(1, 1), tank.getCurrentCoordinates());

        tank.update(0.2f);
        assertEquals(1f, tank.getMovementProgress(), 0f);
        assertEquals(new GridPoint2(2, 1), tank.getCurrentCoordinates());
    }

    @Test
    public void blockedMoveOnlyTurnsTank() {
        Tank tank = new Tank(new GridPoint2(1, 1), 0.4f);
        Obstacle obstacle = new Obstacle(new GridPoint2(1, 2));

        move(tank, Direction.UP, obstacle);

        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
        assertEquals(1f, tank.getMovementProgress(), 0f);
        assertEquals(90f, tank.getRotation(), 0f);
    }

    @Test
    public void ignoresNewMoveWhileAlreadyMoving() {
        Tank tank = new Tank(new GridPoint2(1, 1), 0.4f);
        move(tank, Direction.UP);

        move(tank, Direction.LEFT);

        assertEquals(new GridPoint2(1, 2), tank.getDestinationCoordinates());
        assertEquals(90f, tank.getRotation(), 0f);
    }

    @Test
    public void clampsProgressAndProtectsCoordinates() {
        GridPoint2 source = new GridPoint2(1, 1);
        Tank tank = new Tank(source, 0.4f);
        source.set(9, 9);
        tank.getCurrentCoordinates().set(8, 8);
        tank.getDestinationCoordinates().set(7, 7);

        assertEquals(new GridPoint2(1, 1), tank.getCurrentCoordinates());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());

        move(tank, Direction.RIGHT);
        tank.update(1f);
        assertEquals(1f, tank.getMovementProgress(), 0f);
        assertEquals(new GridPoint2(2, 1), tank.getCurrentCoordinates());
    }

    private void move(Tank tank, Direction direction, Obstacle... obstacles) {
        WorldView world = new WorldView() {
            @Override
            public TankView getTank() {
                return tank;
            }

            @Override
            public List<ObstacleView> getObstacles() {
                return Arrays.asList(obstacles);
            }
        };
        tank.tryMove(direction, new ObstacleMovementPolicy(), world);
    }
}
