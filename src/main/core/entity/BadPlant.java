package core.entity;

import core.coordinates.XY;

public class BadPlant extends Entity {

    private static final int START_ENERGY = -100;

    public BadPlant(int id, XY position) {
        super(id, START_ENERGY, position);
    }
}
