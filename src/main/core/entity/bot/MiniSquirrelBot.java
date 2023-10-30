package core.entity.bot;

import botapi.BotController;
import botapi.BotControllerFactory;
import botapi.ControllerContext;
import botapi.ControllerContextHandler;
import core.coordinates.Direction;
import core.coordinates.XY;
import core.entity.EntityContext;
import core.entity.EntityType;
import core.entity.squirrel.MasterSquirrel;
import core.entity.squirrel.MiniSquirrel;
import core.entity.squirrel.NotEnoughEnergyException;

import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MiniSquirrelBot extends MiniSquirrel {

    private final BotController botController;
    private final ControllerContext controllerContext;
    private EntityContext view;
    private boolean commandUsed;

    public MiniSquirrelBot(int id, int energy, XY position, MasterSquirrel owner, BotControllerFactory factory) {
        super(id, energy, position, owner);
        this.controllerContext = (ControllerContext) Proxy.newProxyInstance(
                ControllerContext.class.getClassLoader(),
                new Class[]{ControllerContext.class},
                new ControllerContextHandler(new ControllerContextImpl()));
        this.botController = factory.createMiniBotController();
    }

    @Override
    public void nextStep(EntityContext context) throws NotEnoughEnergyException {
        this.view = context;
        if (coolDown-- <= 0) {
            commandUsed = false;
            botController.nextStep(controllerContext);
            updateEnergy(-1);
            if (energy <= 0)
                context.kill(this);
        }
    }

    private class ControllerContextImpl implements ControllerContext {

        @Override
        public XY getViewLowerLeft() {
            return new XY(
                    Math.max(position.getX() - view.getMiniFOV().getX() / 2, 0),
                    Math.min(position.getY() + view.getMiniFOV().getY() / 2, view.getSize().getY())
            );
        }

        @Override
        public XY getViewUpperRight() {
            return new XY(
                    Math.min(position.getX() + view.getMiniFOV().getX() / 2, view.getSize().getX()),
                    Math.max(position.getY() - view.getMiniFOV().getY() / 2, 0)
            );
        }

        @Override
        public XY locate() {
            return MiniSquirrelBot.this.position;
        }

        @Override
        public EntityType getEntityAt(XY xy) {
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
                        && ((MiniSquirrel) view.getEntity(xy)).getOwner().equals(MiniSquirrelBot.this.getOwner())) {
                    return true;
                } else return view.getEntity(xy) instanceof MasterSquirrel
                        && view.getEntity(xy).equals(MiniSquirrelBot.this.getOwner());
            } else throw new OutOfFovException();
        }

        @Override
        public void move(Direction dir) {
            if(commandUsed)
                return;
            view.tryMove(MiniSquirrelBot.this, dir.getVector());
            commandUsed = true;
        }

        @Override
        public void spawnMiniBot(XY direction, int energy) {
            commandUsed = true;
            throw new UnsupportedOperationException("Mini squirrels can't spawn mini squirrels!");
        }

        @Override
        public void implode(int impactRadius) throws ImpactOutOfBoundsException {
            if(commandUsed)
                return;
            try {
                view.implodeMini(MiniSquirrelBot.this, impactRadius);
            } catch (ImpactOutOfBoundsException e) {
                e.printStackTrace();
            }
            commandUsed = true;
        }

        @Override
        public int getEnergy() {
            return energy;
        }

        @Override
        public XY directionOfMaster() {

            int x = MiniSquirrelBot.this.getOwner().getPosition().getX() - MiniSquirrelBot.this.position.getX();
            int y = MiniSquirrelBot.this.getOwner().getPosition().getY() - MiniSquirrelBot.this.position.getY();

            return new XY(x / Math.abs(x), y / Math.abs(y));
        }

        @Override
        public long getRemainingSteps() {
            return this.getEnergy();
        }

    }
}
