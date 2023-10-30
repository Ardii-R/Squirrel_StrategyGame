package core.ui;

public class Command {

    public static final Command DO_NOTHING = new Command(GameCommandType.CENTER, new Object[0]);

    private final CommandTypeInfo commandTypeInfo;
    private final Object[] params;

    public Command(CommandTypeInfo commandTypeInfo, Object[] params) {
        this.commandTypeInfo = commandTypeInfo;
        this.params = params;
    }

    public CommandTypeInfo getCommandTypeInfo() {
        return commandTypeInfo;
    }

    public Object[] getParams() {
        return params;
    }
}
