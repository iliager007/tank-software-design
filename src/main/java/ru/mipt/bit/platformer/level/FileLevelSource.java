package ru.mipt.bit.platformer.level;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class FileLevelSource implements LevelSource {

    private final Path path;
    private final TextLevelParser parser;

    public FileLevelSource(Path path, TextLevelParser parser) {
        this.path = Objects.requireNonNull(path, "path");
        this.parser = Objects.requireNonNull(parser, "parser");
    }

    @Override
    public LevelDefinition load() throws IOException {
        return parser.parse(Files.readAllLines(path, StandardCharsets.UTF_8));
    }
}
