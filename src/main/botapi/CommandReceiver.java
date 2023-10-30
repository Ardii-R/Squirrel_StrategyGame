package botapi;

import core.ui.Command;

public interface CommandReceiver {

    void receive(Command command);
}
