package core.entity;

import core.coordinates.XY;
import core.entity.bot.MasterSquirrelBot;
import core.entity.squirrel.MasterSquirrel;
import core.entity.squirrel.MiniSquirrel;
import core.entity.squirrel.NotEnoughEnergyException;

public interface EntityContext {

    XY getSize();

    XY getFOV();

    XY getSquirrelFOV();

    XY getMiniFOV();

    EntityType getEntityType(int x, int y);

    Entity getEntity(XY xy);

    void spawnMiniSquirrel(MiniSquirrel.Data data, MasterSquirrel owner) throws NotEnoughEnergyException;

    void implodeMini(MiniSquirrel miniSquirrel, int impactRadius);

    void tryMove(MiniSquirrel miniSquirrel, XY moveDirection);

    void tryMove(GoodBeast goodBeast, XY moveDirection);

    void tryMove(BadBeast badBeast, XY moveDirection);

    void tryMove(MasterSquirrel master, XY moveDirection);

    void shout(Entity entity, String message);

    void kill(Entity entity);

    void killAndReplace(Entity entity);
}

