package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.controller.ActionSource;
import ru.mipt.bit.platformer.controller.PlayerAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class PlayerInput implements ActionSource {

    private final Input input;
    private final List<KeyBinding> keyBindings;

    public PlayerInput(Input input, List<KeyBinding> keyBindings) {
        this.input = input;
        this.keyBindings = Collections.unmodifiableList(new ArrayList<>(keyBindings));
    }

    @Override
    public Set<PlayerAction> getPressedActions() {
        EnumSet<PlayerAction> actions = EnumSet.noneOf(PlayerAction.class);
        for (KeyBinding binding : keyBindings) {
            if (binding.isPressed(input)) {
                actions.add(binding.getAction());
            }
        }
        return actions;
    }
}
