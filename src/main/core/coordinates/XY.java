package core.coordinates;

import java.util.Objects;

import static java.lang.String.format;

public final class XY {

    public static final XY ZERO_ZERO = new XY( 0 , 0 ) ;
    public static final XY RIGHT = new XY( 1 , 0 ) ;
    public static final XY LEFT = new XY(-1, 0 ) ;
    public static final XY UP = new XY( 0 , -1);
    public static final XY DOWN = new XY( 0 , 1 ) ;
    public static final XY RIGHT_UP = new XY( 1 , -1);
    public static final XY RIGHT_DOWN = new XY( 1 , 1 ) ;
    public static final XY LEFT_UP = new XY(-1, -1);
    public static final XY LEFT_DOWN = new XY(-1, 1 ) ;

    private final int x;
    private final int y;

    public XY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public XY plus(XY xy) {
        return new XY(x + xy.getX(), y + xy.getY());
    }

    public XY minus(XY xy) {
        return new XY(x - xy.getX(), y - xy.getY());
    }

    public double distanceFrom(XY a) {
        return Math.sqrt(Math.pow((Math.abs(x - a.getX())), 2) + Math.pow(Math.abs(y - a.getY()), 2));
    }

    public XY times(int factor) {
        return new XY(x * factor, y * factor);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var xy = (XY) o;

        return x == xy.x && y == xy.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return format("{ x = %s; y = %s; }", x, y);
    }
}
