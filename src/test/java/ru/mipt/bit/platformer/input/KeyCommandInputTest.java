package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class KeyCommandInputTest {

    @Test
    public void executesCommandOnlyOnNewPressOfConfiguredKey() {
        boolean[] pressed = {false};
        boolean[] justPressed = {false};
        Input input = (Input) Proxy.newProxyInstance(Input.class.getClassLoader(), new Class<?>[]{Input.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("isKeyJustPressed")) {
                        assertEquals(Input.Keys.L, args[0]);
                        return justPressed[0];
                    }
                    if (method.getName().equals("isKeyPressed")) {
                        return pressed[0];
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        AtomicInteger executions = new AtomicInteger();
        KeyCommandInput commands = new KeyCommandInput(input, Input.Keys.L, executions::incrementAndGet);

        commands.update();
        assertEquals(0, executions.get());
        pressed[0] = true;
        justPressed[0] = true;
        commands.update();
        assertEquals(1, executions.get());
        justPressed[0] = false;
        commands.update();
        commands.update();
        assertEquals(1, executions.get());
        pressed[0] = false;
        commands.update();
        pressed[0] = true;
        justPressed[0] = true;
        commands.update();
        assertEquals(2, executions.get());
    }
}
