package core.entity.squirrel;

import core.coordinates.XY;
import core.entity.Character;

import java.util.Objects;

public abstract class Squirrel extends Character {

    protected int coolDown = 0;

    protected Squirrel(int id, int energy, XY position) {
        super(id, energy, position);
    }

    public void activateCoolDown() {
        coolDown = 3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        var squirrel = (Squirrel) o;

        return coolDown == squirrel.coolDown;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), coolDown);
    }
}
