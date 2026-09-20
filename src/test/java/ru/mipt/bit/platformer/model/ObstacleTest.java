package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObstacleTest {

    @Test
    public void detectsItsCell() {
        Obstacle obstacle = new Obstacle(new GridPoint2(1, 3));

        assertTrue(obstacle.isLocatedAt(new GridPoint2(1, 3)));
        assertFalse(obstacle.isLocatedAt(new GridPoint2(3, 1)));
    }

    @Test
    public void protectsCoordinatesFromExternalChanges() {
        GridPoint2 source = new GridPoint2(1, 3);
        Obstacle obstacle = new Obstacle(source);

        source.set(4, 4);
        obstacle.getCoordinates().set(5, 5);

        assertEquals(new GridPoint2(1, 3), obstacle.getCoordinates());
    }
}
