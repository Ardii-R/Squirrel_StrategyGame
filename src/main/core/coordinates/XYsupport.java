package core.coordinates;

import java.util.concurrent.ThreadLocalRandom;

public class XYsupport {

    public static XY getRandomPosition(int xBound, int yBound) {
        return new XY(ThreadLocalRandom.current().nextInt(xBound), ThreadLocalRandom.current().nextInt(yBound));
    }

    public static XY negateVector(XY vector) {
        return new XY(-1 * vector.getX(), -1 * vector.getY());
    }

    public static XY getRandomDirectionVector() {
        return Direction.values()[ThreadLocalRandom.current().nextInt(Direction.values().length)].getVector();
    }

    public static XY normalize(XY xy) {
        return new XY(xy.getX() / Math.abs(xy.getX()), xy.getY() / Math.abs(xy.getY()));
    }

}
