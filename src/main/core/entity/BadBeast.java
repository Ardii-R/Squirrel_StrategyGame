package core.entity;

import core.coordinates.XY;
import core.coordinates.XYsupport;

import java.util.Objects;

public class BadBeast extends Character {

    private static final int START_ENERGY = -150;
    private int stepCounter = 0;
    private int bitesLeft = 7;

    public BadBeast(int id, XY position) {
        super(id, START_ENERGY, position);
    }

    @Override
    public void nextStep(EntityContext context) {
        if (stepCounter++ % 4 == 0) {
            int viewX = context.getFOV().getX() / 2;
            int viewY = context.getFOV().getY() / 2;

            for (int y = getPosition().getY() - viewY; y < getPosition().getY() + viewY; y++) {
                for (int x = getPosition().getX() - viewX; x < getPosition().getX() + viewX; x++) {
                    if (context.getEntityType(x, y) == EntityType.MASTERSQUIRREL
                            || context.getEntityType(x, y) == EntityType.MINISQUIRREL) {
                        var x2 = Integer.compare(x, getPosition().getX());
                        var y2 = Integer.compare(y, getPosition().getY());

                        context.tryMove(this, new XY(x2, y2));
                        return;
                    }
                }
            }
            context.tryMove(this, XYsupport.getRandomDirectionVector());
        }
    }

    public void bite() {
        bitesLeft--;
    }

    public int getBitesLeft() {
        return bitesLeft;
    }

    public void setBitesLeft(int bitesLeft) {
        this.bitesLeft = bitesLeft;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        var badBeast = (BadBeast) o;

        return bitesLeft == badBeast.bitesLeft;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bitesLeft);
    }
}
