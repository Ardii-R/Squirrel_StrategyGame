package core.entity.squirrel;

import core.coordinates.XY;
import core.coordinates.XYsupport;
import core.entity.EntityContext;

import java.util.Objects;

public class MiniSquirrel extends Squirrel {

    private final MasterSquirrel owner;

    public MiniSquirrel(int id, int energy, XY position, MasterSquirrel owner) {
        super(id, energy, position);
        this.owner = owner;
    }

    public MasterSquirrel getOwner() {
        return owner;
    }

    @Override
    public void nextStep(EntityContext context) throws NotEnoughEnergyException {
        if (coolDown-- <= 0) {
            context.tryMove(this, XYsupport.getRandomDirectionVector());
            updateEnergy(-1);
            if (energy <= 0)
                context.kill(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MiniSquirrel that = (MiniSquirrel) o;

        return owner.equals(that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), owner);
    }

    public static class Data {

        private final int energy;
        private final XY direction;

        public Data(int energy, XY direction) {
            this.energy = energy;
            this.direction = direction;
        }

        public int getEnergy() {
            return energy;
        }

        public XY getDirection() {
            return direction;
        }

    }
}
