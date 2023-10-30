package core.entity.bot.factory.handoperated;

import botapi.BotController;
import botapi.BotControllerFactory;
import botapi.CommandReceiver;
import botapi.ControllerContext;
import core.coordinates.Direction;
import core.coordinates.XY;
import core.entity.squirrel.MiniSquirrel;
import core.entity.squirrel.NotEnoughEnergyException;
import core.ui.Command;
import core.ui.GameCommandType;

import java.util.Arrays;
import java.util.Locale;

public class HandOperatedSquirrel implements CommandReceiver, BotControllerFactory {

    private boolean implode;

    Handoperated master = new Handoperated() {

        private Command lastCmd;

        @Override
        public void setLastCmd(Command command) {
            this.lastCmd = command;
        }

        @Override
        public void nextStep(ControllerContext view) throws NotEnoughEnergyException {
            implode = false;
            if (lastCmd == null) return;
            Object[] params = lastCmd.getParams();
            GameCommandType commandType = (GameCommandType) lastCmd.getCommandTypeInfo();

            switch (commandType) {
                case CENTER, UP, TOP_LEFT, TOP_RIGHT, LEFT, RIGHT, DOWN, BOTTOM_LEFT, BOTTOM_RIGHT -> {
                    view.move( Arrays.stream(Direction.class.getEnumConstants())
                            .filter(constant -> constant.name().equals(commandType.getName().toUpperCase(Locale.ROOT))
                                    || String.valueOf(constant.getNumPadKey()).equals(commandType.name()))
                            .findAny()
                            .orElse(Direction.CENTER));
                }
                case SPAWN_MINI -> {
                    view.spawnMiniBot((XY)lastCmd.getParams()[1], 100);
                }
                case IMPLODE_MINI -> {
                    implode = true;
                }
            }
        }
    };

    BotController mini = view -> {
        if(implode)
            view.implode(10);
        else
            view.move(Direction.getRandom());
    };

    @Override
    public void receive(Command command) {
        master.setLastCmd(command);
    }

    @Override
    public BotController createMasterBotController() {
        return master;
    }

    @Override
    public BotController createMiniBotController() {
        return mini;
    }
}
