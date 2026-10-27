package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class MoveTankCommandTest {

    @Test
    public void movesReceiverOnlyWhenExecuted() {
        Tank receiver = new Tank(new GridPoint2(1, 1), 0.4f);
        TankCommand command = new MoveTankCommand(Direction.RIGHT);
        assertEquals(new GridPoint2(1, 1), receiver.getDestinationCoordinates());

        command.execute(receiver, new CollisionMovementPolicy(), new GameWorld());
        receiver.update(0.2f);

        assertEquals(new GridPoint2(1, 1), receiver.getCurrentCoordinates());
        assertEquals(new GridPoint2(2, 1), receiver.getDestinationCoordinates());
        assertEquals(0.5f, receiver.getMovementProgress(), 0.0001f);
    }

    @Test
    public void respectsCollisionPolicyAndTurnsWhenBlocked() {
        Tank receiver = new Tank(new GridPoint2(1, 2), 0.4f);

        new MoveTankCommand(Direction.UP).execute(receiver, new CollisionMovementPolicy(), new GameWorld());

        assertEquals(new GridPoint2(1, 2), receiver.getDestinationCoordinates());
        assertEquals(1f, receiver.getMovementProgress(), 0f);
        assertEquals(90f, receiver.getRotation(), 0f);
        assertThrows(NullPointerException.class, () -> new MoveTankCommand(null));
    }
}
