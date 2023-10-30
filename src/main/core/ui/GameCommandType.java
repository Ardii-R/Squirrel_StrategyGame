package core.ui;

import java.util.regex.Pattern;

public enum GameCommandType implements CommandTypeInfo {
    HELP("help", " - List all commands", Pattern.compile("\\Qhelp\\E"), 0),
    EXIT("exit", " - Exit the game", Pattern.compile("\\Qexit\\E"), 0),
    LOG("log", " - ???", Pattern.compile("\\Qall\\E"), 0),
    LEFT("left", " - Move left", Pattern.compile("\\Qleft\\E|4"), 0),
    UP("up", " - Move up", Pattern.compile("\\Qup\\E|8"), 0),
    DOWN("down", " - Move down", Pattern.compile("\\Qdown\\E|2"), 0),
    RIGHT("right", " - Move right", Pattern.compile("\\Qright\\E|6"), 0),
    CENTER("center", " - Stay where you are", Pattern.compile("\\Qcenter\\E|5"), 0),
    TOP_RIGHT("top_right", " - Move top right", Pattern.compile("\\Qtop_right\\E|9"), 0),
    TOP_LEFT("top_left", " - Move top left", Pattern.compile("\\Qtop_left\\E|7"), 0),
    BOTTOM_RIGHT("bottom_right", " - Move bottom right", Pattern.compile("\\Qbottom_right\\E|3"), 0),
    BOTTOM_LEFT("bottom_left", " - Move bottom left", Pattern.compile("\\Qbottom_left\\E|1"), 0),
    MASTER_ENERGY("master_energy", " - Display your energy", Pattern.compile("\\Qmaster_energy\\E"), 0),
    SPAWN_MINI("spawn_mini", " - Spawn a mini squirrel with the specified energy and direction",
            Pattern.compile("\\Qspawn_mini\\E\\s+\\d+\\s+(left|right|down|up|rand)"), 2),
    IMPLODE_MINI("implode_mini", " - Implodes Mini-Squirrel with given id and radius",
            Pattern.compile("\\Qimpode_mini\\E\\s+\\d+\\s+\\d+"), 2);

    private final String name;
    private final String helpText;
    private final Pattern regex;
    private final int paramsSize;

    GameCommandType(String name, String helpText, Pattern regex, int paramsSize) {
        this.name = name;
        this.helpText = helpText;
        this.regex = regex;
        this.paramsSize = paramsSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHelpText() {
        return helpText;
    }

    @Override
    public Pattern getRegex() {
        return regex;
    }

    @Override
    public int getParamsSize() {
        return paramsSize;
    }
}
