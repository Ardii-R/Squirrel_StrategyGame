package botapi;

import core.entity.squirrel.NotEnoughEnergyException;

public interface BotController {

    void nextStep(ControllerContext view) throws NotEnoughEnergyException;

}
