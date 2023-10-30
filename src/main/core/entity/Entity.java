package core.entity;

import core.coordinates.XY;
import core.entity.squirrel.NotEnoughEnergyException;

import java.util.Objects;

import static java.lang.String.format;

public abstract class Entity {

    protected final int id;
    protected int energy;
    protected XY position;

    protected Entity(int id, int energy, XY position) {
        this.id = id;
        this.energy = energy;
        this.position = position;
    }

    public int getEnergy() {
        return energy;
    }

    public XY getPosition() {
        return position;
    }

    public void setPosition(XY position) {
        this.position = position;
    }

    public void updateEnergy(int delta) {
        energy += delta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return id == ((Entity)o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, energy, position);
    }

    @Override
    public String toString() {
        return format("""
                        [%s = {
                                id = %s;
                                energy = %s;
                                position = %s;
                            }
                        ]""",
                getClass().getSimpleName(),
                id,
                energy,
                position);
    }
}
