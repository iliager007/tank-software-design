package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DirectionTest {

    @Test
    public void movesOneCellWithoutChangingSource() {
        GridPoint2 source = new GridPoint2(2, 3);

        assertEquals(new GridPoint2(2, 4), Direction.UP.move(source));
        assertEquals(new GridPoint2(1, 3), Direction.LEFT.move(source));
        assertEquals(new GridPoint2(2, 2), Direction.DOWN.move(source));
        assertEquals(new GridPoint2(3, 3), Direction.RIGHT.move(source));
        assertEquals(new GridPoint2(2, 3), source);
    }

    @Test
    public void exposesRotationForEachDirection() {
        assertEquals(90f, Direction.UP.getRotation(), 0f);
        assertEquals(-180f, Direction.LEFT.getRotation(), 0f);
        assertEquals(-90f, Direction.DOWN.getRotation(), 0f);
        assertEquals(0f, Direction.RIGHT.getRotation(), 0f);
    }
}
