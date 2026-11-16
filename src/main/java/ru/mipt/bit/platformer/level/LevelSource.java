package ru.mipt.bit.platformer.level;

import java.io.IOException;

/**
 * Application: исходящий порт получения определения уровня; пока вызывается из launcher.
 */
@FunctionalInterface
public interface LevelSource {

    LevelDefinition load() throws IOException;
}
