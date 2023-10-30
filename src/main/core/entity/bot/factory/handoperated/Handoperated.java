package core.entity.bot.factory.handoperated;

import botapi.BotController;
import core.ui.Command;

public interface Handoperated extends BotController {

    void setLastCmd(Command command);

}
