package core.entity;

import core.coordinates.XY;

public class GoodPlant extends Entity {

    private static final int START_ENERGY = 100;

    public GoodPlant(int id, XY position) {
        super(id, START_ENERGY, position);
    }
}
