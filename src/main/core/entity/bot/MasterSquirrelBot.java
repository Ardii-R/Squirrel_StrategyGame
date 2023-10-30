package core.entity.bot;

import botapi.BotController;
import botapi.BotControllerFactory;
import botapi.ControllerContext;
import botapi.ControllerContextHandler;
import core.coordinates.Direction;
import core.coordinates.XY;
import core.entity.Character;
import core.entity.EntityContext;
import core.entity.EntityType;
import core.entity.squirrel.MasterSquirrel;
import core.entity.squirrel.MiniSquirrel;
import core.entity.squirrel.NotEnoughEnergyException;

import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MasterSquirrelBot extends MasterSquirrel {

    private final ControllerContext controllerContext;
    private final BotController botController;
    private EntityContext view;
    private boolean commandUsed;
    private final BotControllerFactory factory;

    public MasterSquirrelBot(int id, XY position, BotControllerFactory botControllerFactory) {
        super(id, position);
        this.factory = botControllerFactory;
        controllerContext = (ControllerContext) Proxy.newProxyInstance(
                ControllerContext.class.getClassLoader(),
                new Class[]{ControllerContext.class},
                new ControllerContextHandler(new ControllerContextImpl()));
        botController = factory.createMasterBotController();
    }

    public BotControllerFactory getFactory() {
        return factory;
    }

    @Override
    public void nextStep(EntityContext context) throws NotEnoughEnergyException {
        this.view = context;
        if (coolDown-- <= 0) {
            commandUsed = false;
            botController.nextStep(controllerContext);
        }
    }

    private class ControllerContextImpl implements ControllerContext {

        @Override
        public XY getViewLowerLeft() {
            return new XY(
                    Math.max(position.getX() - view.getSquirrelFOV().getX() / 2, 0),
                    Math.min(position.getY() + view.getSquirrelFOV().getY() / 2, view.getSize().getY())
            );
        }

        @Override
        public XY getViewUpperRight() {
            return new XY(
                    Math.min(position.getX() + view.getSquirrelFOV().getX() / 2, view.getSize().getX()),
                    Math.max(position.getY() - view.getSquirrelFOV().getY() / 2, 0)
            );
        }

        @Override
        public XY locate() {
            return MasterSquirrelBot.this.position;
        }

        @Override
        public EntityType getEntityAt(XY xy) throws OutOfFovException {
            if ((getViewLowerLeft().getX() <= xy.getX() && xy.getX() <= getViewUpperRight().getX())
                    && (getViewUpperRight().getY() <= xy.getY() && xy.getY() <= getViewLowerLeft().getY()))
                return view.getEntityType(xy.getX(), xy.getY());
            else
                throw new OutOfFovException();
        }

        @Override
        public boolean isMine(XY xy) throws OutOfFovException {

            if((getViewLowerLeft().getX() <= xy.getX() && xy.getX() <= getViewUpperRight().getX())
                    && getViewUpperRight().getY() <= xy.getY() && xy.getY() <= getViewLowerLeft().getY()) {
                if (view.getEntity(xy) instanceof MiniSquirrel
                        && ((MiniSquirrel) view.getEntity(xy)).getOwner().equals(MasterSquirrelBot.this)) {
                    return true;
                } else return view.getEntity(xy) instanceof MasterSquirrel
                        && view.getEntity(xy).equals(MasterSquirrelBot.this);
            } else throw new OutOfFovException();
        }

        @Override
        public void move(Direction direction) {
            if(commandUsed)
                return;
            view.tryMove(MasterSquirrelBot.this, direction.getVector());
            commandUsed = true;
        }

        @Override
        public void spawnMiniBot(XY direction, int energy){
            if(commandUsed)
                return;
            try {
                view.spawnMiniSquirrel(new MiniSquirrel.Data(energy, direction), MasterSquirrelBot.this);
            } catch (NotEnoughEnergyException e) {
                e.printStackTrace();
            }
            commandUsed = true;
        }

        @Override
        public void implode(int impactRadius) {
            commandUsed = true;
            try {
                throw new IllegalAccessException("Masters can't implode");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getEnergy() {
            return energy;
        }

        @Override
        public XY directionOfMaster() {
            return XY.ZERO_ZERO;
        }

        @Override
        public long getRemainingSteps() {
            return Long.MAX_VALUE;
        }

    }
}
