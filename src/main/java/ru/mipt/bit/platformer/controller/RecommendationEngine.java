package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.model.TankView;
import ru.mipt.bit.platformer.model.WorldView;

import java.util.Optional;

/**
 * Application: исходящий порт рекомендаций; адаптер конкретного внешнего движка отсутствует.
 */
@FunctionalInterface
public interface RecommendationEngine {

    Optional<Recommendation> recommend(TankView tank, WorldView world);
}
