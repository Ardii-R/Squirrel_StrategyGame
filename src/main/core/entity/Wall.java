package core.entity;

import core.coordinates.XY;

public class Wall extends Entity {

    private static final int START_ENERGY = -10;

    public Wall(int id, XY position) {
        super(id, START_ENERGY, position);
    }
}
