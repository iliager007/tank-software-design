package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;

public final class KeyCommandInput {

    private final Input input;
    private final int key;
    private final Runnable command;

    public KeyCommandInput(Input input, int key, Runnable command) {
        this.input = input;
        this.key = key;
        this.command = command;
    }

    public void update() {
        if (input.isKeyJustPressed(key)) {
            command.run();
        }
    }
}
