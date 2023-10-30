package botapi;

import core.coordinates.Direction;
import core.coordinates.XY;
import core.entity.EntityType;
import core.entity.bot.ImpactOutOfBoundsException;
import core.entity.bot.OutOfFovException;
import core.entity.squirrel.NotEnoughEnergyException;

public interface ControllerContext {

    XY getViewLowerLeft();

    XY getViewUpperRight();

    XY locate();

    EntityType getEntityAt(XY xy);

    boolean isMine(XY xy) throws OutOfFovException;

    void move(Direction direction);

    void spawnMiniBot(XY direction, int energy) throws OutOfFovException;

    void implode(int impactRadius) throws ImpactOutOfBoundsException;

    int getEnergy();

    XY directionOfMaster();

    long getRemainingSteps();

    default void shout(String text) {
        System.out.println(text);
    };

}
