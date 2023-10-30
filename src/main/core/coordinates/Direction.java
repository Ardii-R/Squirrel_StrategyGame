package core.coordinates;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.String.format;

public enum Direction {
    UP(8, new XY(0, -1)),
    DOWN(2, new XY(0, 1)),
    LEFT(4, new XY(-1, 0)),
    RIGHT(6, new XY(1, 0)),
    CENTER(5, new XY(0, 0)),
    TOP_RIGHT(9, new XY(1, -1)),
    TOP_LEFT(7, new XY(-1, -1)),
    BOTTOM_RIGHT(3, new XY(1, 1)),
    BOTTOM_LEFT(1, new XY(-1, 1));

    private final int numPadKey;
    private final XY vector;

    Direction(int key, XY vector) {
        this.numPadKey = key;
        this.vector = vector;
    }

    public static Direction getRandom() {
        return values()[ThreadLocalRandom.current().nextInt(values().length)];
    }

    public int getNumPadKey() {
        return numPadKey;
    }

    public XY getVector() {
        return vector;
    }

    @Override
    public String toString() {
        return format("[%s = %s (KeyMap=%s)]", getClass().getSimpleName(), name(), numPadKey);
    }
}
