package core.entity;

import core.coordinates.XY;
import core.coordinates.XYsupport;

import java.util.Objects;

public class GoodBeast extends Character {

    private static final int START_ENERGY = 200;
    private int stepCounter = 0;

    public GoodBeast(int id, XY position) {
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

                        context.tryMove(this, XYsupport.negateVector(new XY(x2, y2)));
                        return;
                    }
                }
            }
            context.tryMove(this, XYsupport.getRandomDirectionVector());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        var goodBeast = (GoodBeast) o;

        return stepCounter == goodBeast.stepCounter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), stepCounter);
    }
}
