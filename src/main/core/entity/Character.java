package core.entity;

import core.coordinates.XY;
import core.entity.bot.OutOfFovException;
import core.entity.squirrel.NotEnoughEnergyException;

public abstract class Character extends Entity {

    protected Character(int id, int energy, XY position) {
        super(id, energy, position);
    }

    public abstract void nextStep(EntityContext context) throws NotEnoughEnergyException, OutOfFovException;

}
