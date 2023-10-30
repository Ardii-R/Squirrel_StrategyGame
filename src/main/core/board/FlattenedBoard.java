package core.board;


import Launch.Launcher;
import core.coordinates.XY;
import core.coordinates.XYsupport;
import core.entity.*;
import core.entity.bot.ImpactOutOfBoundsException;
import core.entity.bot.MasterSquirrelBot;
import core.entity.bot.MiniSquirrelBot;
import core.entity.squirrel.MasterSquirrel;
import core.entity.squirrel.MiniSquirrel;
import core.entity.squirrel.NotEnoughEnergyException;
import core.entity.squirrel.Squirrel;
import core.ui.gui.assets.Implosion;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlattenedBoard implements EntityContext, BoardView {


    private final Board board;
    private final Entity[][] flatBoard;

    private static final List<Implosion> currentImplosions = new ArrayList<>();;
    private static final Logger logger = Logger.getLogger(FlattenedBoard.class.getName());

    public FlattenedBoard(Board board, int length, int width) {
        this.board = board;
        this.flatBoard = new Entity[width][length];
        for (Entity entity : board.getEntities())
            flatBoard[entity.getPosition().getY()][entity.getPosition().getX()] = entity;
    }

    private void setPosition(Entity entity, XY newPos) {
        logger.info("Setting position of entity " + entity + " to " + newPos);
        flatBoard[entity.getPosition().getY()][entity.getPosition().getX()] = null;
        entity.setPosition(newPos);
        flatBoard[entity.getPosition().getY()][entity.getPosition().getX()] = entity;
    }

    @Override
    public EntityType getEntityTypeAt(int x, int y) {
        if (x >= flatBoard[0].length || y >= flatBoard.length || x < 0 || y < 0)
            return null;
        var entity = flatBoard[y][x];

        if (entity instanceof MasterSquirrel)
            return EntityType.MASTERSQUIRREL;
        else if(entity instanceof MiniSquirrel)
            return EntityType.MINISQUIRREL;

        return (entity == null
                ? null
                : EntityType.valueOf(entity.getClass().getSimpleName().toUpperCase(Locale.ENGLISH)));
    }

    @Override
    public List<Implosion> getImplosions() {
        return currentImplosions;
    }

    @Override
    public XY getFOV() {
        return new XY(6, 6);
    }

    @Override
    public XY getSquirrelFOV() {
        return new XY(31, 31);
    }

    @Override
    public XY getMiniFOV() {
        return new XY(21, 21);
    }

    @Override
    public EntityType getEntityType(int x, int y) {
        return getEntityTypeAt(x, y);
    }

    @Override
    public  Entity getEntity(XY xy) {
        return flatBoard[xy.getY()][xy.getX()];
    }

    @Override
    public XY getSize() {
        return new XY(flatBoard[0].length, flatBoard.length);
    }

    @Override
    public void spawnMiniSquirrel(MiniSquirrel.Data data, MasterSquirrel owner) throws NotEnoughEnergyException {

        int energy = data.getEnergy();
        var position = owner.getPosition().plus(data.getDirection());
        var type = getEntityTypeAt(position.getX(), position.getY());

        if (type == EntityType.WALL || flatBoard[position.getY()][position.getX()] instanceof Squirrel) {
            logger.warning("Failed: " + type + " at current position!");
            return;
        }

        if (energy < 100 || energy > owner.getEnergy()) {
            logger.log(Level.WARNING, "Failed: ", new NotEnoughEnergyException());
            throw new NotEnoughEnergyException();
        }

        Entity newEntity;
        //boolean HAND_OPERATED = Launch.Launcher.getHAND_OPERATED();
        if(Launcher.HAND_OPERATED)
            newEntity = new MiniSquirrel(board.getNextId(), energy, position, owner);
        else
            newEntity = new MiniSquirrelBot(board.getNextId(), energy, position, owner, ((MasterSquirrelBot)owner).getFactory());
        board.getEntities().add(newEntity);
        flatBoard[position.getY()][position.getX()] = newEntity;
        owner.updateEnergy(-newEntity.getEnergy());

        logger.info("Spawned mini Squirrel at " + position + " with " + energy + " Energy");
    }

    @Override
    public void implodeMini(MiniSquirrel miniSquirrel, int impactRadius) throws ImpactOutOfBoundsException {
        logger.info("Imploding Mini-Squirrel " + miniSquirrel + " with radius " + impactRadius);
        // --Just radia between 2 and 10
        if( impactRadius < 2 || 10 < impactRadius ) {
            logger.log(Level.WARNING, "Failed to implode Mini Squirrel: ", new ImpactOutOfBoundsException());
            throw new ImpactOutOfBoundsException();
        }

        // current mini position
        int x = miniSquirrel.getPosition().getX();
        int y = miniSquirrel.getPosition().getY();

        // --Jihadi MiniSquirrel
        kill(miniSquirrel);

        currentImplosions.add(new Implosion(new XY(x - impactRadius, y - impactRadius), impactRadius));

        // --impact Area
        double impactArea = Math.pow( impactRadius, 2 ) * Math.PI;
        int energyCounter = 0;

        // --X
        for(int i = x - impactRadius; i <= x + impactRadius; i++) {
            // --Y
            for(int j = y - impactRadius; j <= y + impactRadius; j++) {
                // Distance <= radius?
                double distance = miniSquirrel.getPosition().distanceFrom(new XY(i, j));
                if(distance <= impactRadius && !(x == i && y == j)) {
                    EntityType entityType = getEntityTypeAt(i, j);
                    // --no collision with walls or air
                    if (entityType == null || entityType == EntityType.WALL) continue;

                    Entity entity = flatBoard[j][i];
                    // --friendly fire is turned off
                    if (entity.equals(miniSquirrel.getOwner()))
                        continue;
                    else if(entityType == EntityType.MINISQUIRREL && miniSquirrel.getOwner().equals(((MiniSquirrel)entity).getOwner()))
                        continue;

                    // --raw energyLoss
                    int energyLoss = (int)( 200 * ( miniSquirrel.getEnergy() / impactArea ) * ( 1 - distance/impactRadius ));
                    // --real energyLoss for specific entity
                    int energyDelta = Math.min(energyLoss, Math.abs(entity.getEnergy()));

                    logger.info("Entity " + entity + " got hit by imploding Mini-Squirrel with energy loss: " + energyLoss);
                    // --lose energy
                    entity.updateEnergy((entity.getEnergy() < 0 ? 1 : -1) * energyDelta);
                    // --give energy to master
                    miniSquirrel.getOwner().updateEnergy(energyDelta);

                    logger.info("Entity after impact: " + entity);
                    //  --kill entities with 0 energy, except for masters
                    //  --replace them, except for minis
                    if(entity.getEnergy() == 0) {
                        switch(entityType) {
                            case MINISQUIRREL -> kill(entity);
                            case MASTERSQUIRREL -> {}
                            default -> killAndReplace(entity);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tryMove(MiniSquirrel miniSquirrel, XY moveDirection) {
        var destination = miniSquirrel.getPosition().plus(moveDirection);
        var type = getEntityType(destination.getX(), destination.getY());
        var entity = flatBoard[destination.getY()][destination.getX()];

        logger.info("Trying to move " + miniSquirrel + " to " + destination);

        if (type != null) {
            switch (type) {
                case GOODPLANT, BADPLANT, GOODBEAST -> {
                    miniSquirrel.updateEnergy(entity.getEnergy());
                    killAndReplace(entity);
                }
                case BADBEAST -> {
                    miniSquirrel.updateEnergy(entity.getEnergy());
                    ((BadBeast) entity).bite();
                    if (((BadBeast) entity).getBitesLeft() <= 0)
                        killAndReplace(entity);
                    else
                        destination = miniSquirrel.getPosition();
                }
                case WALL -> {
                    miniSquirrel.activateCoolDown();
                    miniSquirrel.updateEnergy(entity.getEnergy());
                    destination = miniSquirrel.getPosition();
                }
                case MASTERSQUIRREL -> {
                    if (((MasterSquirrel) entity).isOwner(miniSquirrel))
                        entity.updateEnergy(miniSquirrel.getEnergy());
                    kill(miniSquirrel);
                    return;
                }
                case MINISQUIRREL -> {
                    if (!((MiniSquirrel) entity).getOwner().equals(miniSquirrel.getOwner())) {
                        kill(miniSquirrel);
                        kill(entity);
                        return;
                    }
                }
            }
        }
        if(miniSquirrel.getEnergy() <= 0) {
            kill(miniSquirrel);
            return;
        }
        setPosition(miniSquirrel, destination);
    }

    @Override
    public void tryMove(GoodBeast goodBeast, XY moveDirection) {
        var destination = goodBeast.getPosition().plus(moveDirection);
        var type = getEntityTypeAt(destination.getX(), destination.getY());

        logger.info("Trying to move " + goodBeast + " to " + destination);

        if (type == EntityType.MASTERSQUIRREL || type == EntityType.MINISQUIRREL) {
            flatBoard[destination.getY()][destination.getX()].updateEnergy(goodBeast.getEnergy());
            killAndReplace(goodBeast);
        } else if (type == null)
            setPosition(goodBeast, destination);
    }

    @Override
    public void tryMove(BadBeast badBeast, XY moveDirection) {
        var destination = badBeast.getPosition().plus(moveDirection);
        var type = getEntityTypeAt(destination.getX(), destination.getY());

        logger.info("Trying to move " + badBeast + " to " + destination);

        if (type == EntityType.MASTERSQUIRREL || type == EntityType.MINISQUIRREL) {
            flatBoard[destination.getY()][destination.getX()].updateEnergy(badBeast.getEnergy());
            badBeast.bite();
            if (badBeast.getBitesLeft() <= 0)
                killAndReplace(badBeast);
        } else if (type == null)
            setPosition(badBeast, destination);
    }

    @Override
    public void tryMove(MasterSquirrel master, XY moveDirection) {
        var destination = master.getPosition().plus(moveDirection);
        var entity = flatBoard[destination.getY()][destination.getX()];

        logger.info("Trying to move " + master + " to " + destination);

        if (entity != null) {
            switch (getEntityTypeAt(destination.getX(), destination.getY())) {
                case GOODPLANT, BADPLANT, GOODBEAST -> {
                    master.updateEnergy(entity.getEnergy());
                    killAndReplace(entity);
                }
                case BADBEAST -> {
                    master.updateEnergy(entity.getEnergy());
                    ((BadBeast) entity).bite();
                    if (((BadBeast) entity).getBitesLeft() <= 0)
                        killAndReplace(entity);
                    else
                        destination = master.getPosition();
                }
                case WALL -> {
                    master.activateCoolDown();
                    master.updateEnergy(entity.getEnergy());
                    destination = master.getPosition();
                }
                case MINISQUIRREL -> {
                    if (master.isOwner(entity))
                        master.updateEnergy(entity.getEnergy());
                    else
                        master.updateEnergy(150);
                    kill(entity);
                } case MASTERSQUIRREL -> {
                    destination = master.getPosition();
                }
            }
        }
        setPosition(master, destination);
    }

    @Override
    public void shout(Entity entity, String message) {
        //TODO
    }

    @Override
    public void kill(Entity entity) {
        logger.info("Killing entity " + entity);
        flatBoard[entity.getPosition().getY()][entity.getPosition().getX()] = null;
        board.getEntities().remove(entity);
    }

    @Override
    public void killAndReplace(Entity entity) {
        kill(entity);
        var xy = XYsupport.getRandomPosition(flatBoard[0].length, flatBoard.length);
        while (flatBoard[xy.getY()][xy.getX()] != null || xy.equals(entity.getPosition()))
            xy = XYsupport.getRandomPosition(flatBoard[0].length, flatBoard.length);
        try {
            Class<? extends Entity> clazz = entity.getClass();
            Entity newEntity = clazz.cast(clazz.getConstructors()[0].newInstance(board.getNextId(), xy));
            logger.info("Replacing entity " + entity + " with new entity " + newEntity);
            board.getEntities().add(newEntity);
            flatBoard[newEntity.getPosition().getY()][newEntity.getPosition().getX()] = newEntity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
