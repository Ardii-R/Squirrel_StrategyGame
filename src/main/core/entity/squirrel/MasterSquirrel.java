package core.entity.squirrel;

import core.coordinates.XY;
import core.entity.Entity;
import core.entity.EntityContext;

import java.util.Optional;

public abstract class MasterSquirrel extends Squirrel {

    private static final int START_ENERGY = 1000;

    protected MasterSquirrel(int id, XY position) {
        super(id, START_ENERGY, position);
    }

    public boolean isOwner(Entity entity) {
        return entity instanceof MiniSquirrel && equals(((MiniSquirrel) entity).getOwner());
    }

    @Override
    public void updateEnergy(int delta) {
        energy = Math.max(0, energy + delta);
    }
}
