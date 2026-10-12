package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.WorldSettings;

import java.util.ArrayList;
import java.util.List;

public final class TextLevelParser {

    private final float tankMovementDuration;

    public TextLevelParser(float tankMovementDuration) {
        this.tankMovementDuration = tankMovementDuration;
    }

    public LevelDefinition parse(List<String> rows) {
        if (rows.isEmpty() || rows.get(0).isEmpty()) {
            throw new IllegalArgumentException("Level must not be empty");
        }
        int width = rows.get(0).length();
        GridPoint2 tankCoordinates = null;
        List<GridPoint2> obstacles = new ArrayList<>();
        for (int row = 0; row < rows.size(); row++) {
            String line = rows.get(row);
            if (line.length() != width) {
                throw new IllegalArgumentException("Level must be rectangular: row " + (row + 1));
            }
            for (int column = 0; column < width; column++) {
                GridPoint2 coordinates = new GridPoint2(column, rows.size() - row - 1);
                switch (line.charAt(column)) {
                    case 'T':
                        obstacles.add(coordinates);
                        break;
                    case 'X':
                        if (tankCoordinates != null) {
                            throw new IllegalArgumentException("Level must contain exactly one player start (X)");
                        }
                        tankCoordinates = coordinates;
                        break;
                    case '_':
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown level symbol at row " + (row + 1)
                                + ", column " + (column + 1) + ": " + line.charAt(column));
                }
            }
        }
        if (tankCoordinates == null) {
            throw new IllegalArgumentException("Level must contain exactly one player start (X)");
        }
        return new LevelDefinition(width, rows.size(),
                new WorldSettings(tankCoordinates, tankMovementDuration, obstacles));
    }
}
