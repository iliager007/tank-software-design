package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class PressedKeysInput {

    private PressedKeysInput() {
    }

    public static Input withKeys(int... keys) {
        Set<Integer> pressedKeys = Arrays.stream(keys).boxed().collect(Collectors.toSet());
        return (Input) Proxy.newProxyInstance(
                Input.class.getClassLoader(),
                new Class<?>[]{Input.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("isKeyPressed")) {
                        return pressedKeys.contains((Integer) args[0]);
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );
    }
}
