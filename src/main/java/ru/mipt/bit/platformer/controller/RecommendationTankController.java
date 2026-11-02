package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MoveTankCommand;
import ru.mipt.bit.platformer.model.ShootTankCommand;
import ru.mipt.bit.platformer.model.TankCommand;
import ru.mipt.bit.platformer.model.TankController;
import ru.mipt.bit.platformer.model.TankView;
import ru.mipt.bit.platformer.model.WorldView;

import java.util.Objects;
import java.util.Optional;

public final class RecommendationTankController implements TankController {

    private final RecommendationEngine engine;

    public RecommendationTankController(RecommendationEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    @Override
    public Optional<TankCommand> chooseCommand(TankView tank, WorldView world) {
        return engine.recommend(tank, world).map(this::toCommand);
    }

    private TankCommand toCommand(Recommendation recommendation) {
        switch (recommendation) {
            case MOVE_UP:
                return new MoveTankCommand(Direction.UP);
            case MOVE_LEFT:
                return new MoveTankCommand(Direction.LEFT);
            case MOVE_DOWN:
                return new MoveTankCommand(Direction.DOWN);
            case MOVE_RIGHT:
                return new MoveTankCommand(Direction.RIGHT);
            case SHOOT:
                return new ShootTankCommand();
            default:
                throw new IllegalArgumentException("Unsupported recommendation: " + recommendation);
        }
    }
}
