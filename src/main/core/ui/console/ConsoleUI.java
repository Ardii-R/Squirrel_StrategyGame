package core.ui.console;

import core.board.BoardView;
import core.entity.EntityType;
import core.ui.Command;
import core.ui.GameCommandType;
import core.ui.UI;

public class ConsoleUI implements UI {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    private final CommandScanner scanner = new CommandScanner(GameCommandType.values());
    private Command buffer;

    @Override
    public Command getCommand() {
        var command = buffer;
        buffer = null;
        return command;
    }

    @Override
    public void updateCommand() {
        buffer = scanner.next();
    }

    @Override
    public void render(BoardView view) {
        var sb = new StringBuilder("\n");

        for (var y = 0; y < view.getSize().getY(); y++) {
            for (var x = 0; x < view.getSize().getX(); x++) {
                var entity = view.getEntityTypeAt(x, y);
                sb.append(entity == null
                        ? "."
                        : switch (view.getEntityTypeAt(x, y)) {
                    case MASTERSQUIRREL -> ANSI_PURPLE + "S";
                    case MINISQUIRREL -> ANSI_CYAN + "s";
                    case WALL -> {
                        boolean top = (y - 1) >= 0 && view.getEntityTypeAt(x, y - 1) == EntityType.WALL;
                        boolean left = (x - 1) >= 0 && view.getEntityTypeAt(x - 1, y) == EntityType.WALL;
                        boolean right = (x + 1) < view.getSize().getX() && view.getEntityTypeAt(x + 1, y) == EntityType.WALL;
                        boolean bottom = (y + 1) < view.getSize().getY() && view.getEntityTypeAt(x, y + 1) == EntityType.WALL;

                        if (top && left && right && bottom) yield "+";
                        if (left && right && !top && !bottom) yield "-";
                        if ((top || bottom) && !left && !right) yield "|";
                        if ((left && bottom && !top && !right) || (top && right && !left && !bottom))
                            yield "\\";
                        if ((right && bottom && !left && !top) || (top && left && !right && !bottom)) yield "/";
                        if (left && right && bottom) yield "T";
                        if (left && right) yield "I";
                        yield "#";
                    }
                    case GOODBEAST -> ANSI_GREEN + "&";
                    case GOODPLANT -> ANSI_GREEN + "*";
                    case BADBEAST -> ANSI_RED + "<";
                    case BADPLANT -> ANSI_RED + "8";
                })
                        .append(ANSI_RESET);
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }

    @Override
    public void message(String msg) {
        System.out.println(msg);
    }
}
