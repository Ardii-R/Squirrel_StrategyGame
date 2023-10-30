package core.entity.bot.factory.challenge;

import botapi.BotController;
import botapi.BotControllerFactory;
import core.coordinates.Direction;

// Random Direction

public class RandomBot implements BotControllerFactory {

    @Override
    public BotController createMasterBotController() {
        return view -> {
            view.move(Direction.getRandom());
        };
    }

    @Override
    public BotController createMiniBotController() {
        return view -> {
            view.move(Direction.getRandom());
        };
    }

}
