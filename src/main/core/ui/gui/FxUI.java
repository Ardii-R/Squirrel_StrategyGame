package core.ui.gui;

import botapi.CommandReceiver;
import core.board.BoardView;
import core.coordinates.Direction;
import core.coordinates.XY;
import core.coordinates.XYsupport;
import core.entity.EntityType;
import core.ui.Command;
import core.ui.GameCommandType;
import core.ui.UI;
import core.ui.gui.assets.Implosion;
import engine.Game;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static core.ui.gui.assets.EntityAssets.*;
import static core.ui.gui.assets.StructureAssets.WALL;

public class FxUI extends Scene implements UI {

    private static int cellSize = 50;
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private final Canvas boardCanvas;
    private final Label msgLabel;
    private final CommandReceiver[] receivers;
    private Direction lastMoveDirection = Direction.CENTER;

    public FxUI(Parent parent, Canvas boardCanvas, Label msgLabel, CommandReceiver... receivers) {
        super(parent);
        this.boardCanvas = boardCanvas;
        this.msgLabel = msgLabel;
        this.receivers = receivers;
    }

    public static FxUI createInstance(XY boardSize, CommandReceiver... receivers) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        double frameBuffer = System.getProperty("os.name").equals("Mac OS X")
                ? 0.85
                : 0.92;
        double maxHeight = d.getHeight() / boardSize.getY() * frameBuffer;
        double maxWidth = d.getWidth() / boardSize.getX();
        cellSize = Math.min((int) maxWidth, (int) maxHeight);
        var boardCanvas = new Canvas(boardSize.getX() * cellSize, boardSize.getY() * cellSize);
        var statusLabel = new Label("status");
        var top = new VBox();

        top.getChildren().addAll(boardCanvas, statusLabel);
        statusLabel.setText("No events yet");

        final var fxUi = new FxUI(top, boardCanvas, statusLabel, receivers);

        fxUi.setOnKeyPressed(event -> fxUi.keysPressed.add(event.getCode()));
        fxUi.setOnKeyReleased(event -> fxUi.keysPressed.remove(event.getCode()));

        return fxUi;
    }

    @Override
    public void message(final String msg) {
        Platform.runLater(() -> msgLabel.setText(msg));
    }

    @Override
    public Command getCommand() {
        Command current = parseInput();
        for (CommandReceiver receiver : receivers)
            receiver.receive(current);
        return current;
    }

    @Override
    public void updateCommand() {
        // Ignored
    }

    @Override
    public void render(final BoardView view) {
        Platform.runLater(() -> repaintBoardCanvas(view));
    }

    private void repaintBoardCanvas(BoardView view) {
        var gc = boardCanvas.getGraphicsContext2D();
        XY viewSize = view.getSize();

        Image badPlantFrame = BAD_PLANT.getNextFrame();
        Image badBeastFrame = BAD_BEAST.getNextFrame();
        Image goodBeastFrame = GOOD_BEAST.getNextFrame();
        Image goodPlantFrame = GOOD_PLANT.getNextFrame();
        Image miniSquirrelFrame = MINI_SQUIRREL.getNextFrame();
        Image masterSquirrelFrame = MASTER_SQUIRREL.getNextFrame();

        for (var y = 0; y < viewSize.getY(); y++) {
            for (var x = 0; x < viewSize.getX(); x++) {
                var type = view.getEntityTypeAt(x, y);
                gc.drawImage(GRASS.getNextFrame(), x * cellSize, y * cellSize, cellSize, cellSize);
                if (type == null)
                    continue;
                switch (type) {
                    case WALL -> {
                        byte mask = 0b0000;
                        boolean top = (y - 1) >= 0 && view.getEntityTypeAt(x, y - 1) == EntityType.WALL;
                        boolean left = (x - 1) >= 0 && view.getEntityTypeAt(x - 1, y) == EntityType.WALL;
                        boolean right = (x + 1) < view.getSize().getX() && view.getEntityTypeAt(x + 1, y) == EntityType.WALL;
                        boolean bottom = (y + 1) < view.getSize().getY() && view.getEntityTypeAt(x, y + 1) == EntityType.WALL;

                        if (top)
                            mask |= 0b1000;
                        if (left)
                            mask |= 0b0100;
                        if (right)
                            mask |= 0b0010;
                        if (bottom)
                            mask |= 0b0001;

                        gc.drawImage(WALL.getWallFrame(mask), x * cellSize, y * cellSize, cellSize, cellSize);
                    }
                    case BADBEAST -> gc.drawImage(badBeastFrame,
                            x * cellSize, y * cellSize, cellSize, cellSize);
                    case BADPLANT -> gc.drawImage(badPlantFrame,
                            x * cellSize, y * cellSize, cellSize, cellSize);
                    case GOODBEAST -> gc.drawImage(goodBeastFrame,
                            x * cellSize, y * cellSize, cellSize, cellSize);
                    case GOODPLANT -> gc.drawImage(goodPlantFrame,
                            x * cellSize, y * cellSize, cellSize, cellSize);
                    case MINISQUIRREL -> gc.drawImage(miniSquirrelFrame,
                            x * cellSize, y * cellSize, cellSize, cellSize);
                    case MASTERSQUIRREL -> gc.drawImage(masterSquirrelFrame,
                            x * cellSize, y * cellSize, cellSize, cellSize);
                }
                gc.fillText(String.format("(%d/%d)", x, y), x *cellSize, y * cellSize);
            }
        }
        ArrayList<Implosion> deprecatedImplosions = new ArrayList<>();
        for(Implosion implosion : view.getImplosions()) {
            if(implosion.hasNext())
                gc.drawImage(implosion.getNext(),
                        implosion.getStart().getX() * cellSize, implosion.getStart().getY() * cellSize,
                        implosion.getImpactRadius() * 2 * cellSize, implosion.getImpactRadius() * 2 * cellSize);
            else
                deprecatedImplosions.add(implosion);
        }
        view.getImplosions().removeAll(deprecatedImplosions);

    }

    private Command parseInput() {
        if (keysPressed.contains(KeyCode.SPACE))
            return new Command(GameCommandType.SPAWN_MINI,
                    new Object[]{
                            100, lastMoveDirection == Direction.CENTER
                            ? XYsupport.getRandomDirectionVector()
                            : lastMoveDirection.getVector()});
        else if (keysPressed.contains(KeyCode.ESCAPE))
            return new Command(GameCommandType.EXIT, new Object[0]);
        else if(keysPressed.contains(KeyCode.E)) {
            // TODO
            return new Command(GameCommandType.IMPLODE_MINI, new Object[0]);
        }

        byte mask = 0b0000;

        if (keysPressed.contains(KeyCode.W))
            mask |= 0b1000;
        if (keysPressed.contains(KeyCode.A))
            mask |= 0b0100;
        if (keysPressed.contains(KeyCode.S))
            mask |= 0b0001;
        if (keysPressed.contains(KeyCode.D))
            mask |= 0b0010;

        Command cmd = switch (mask) {
            case 0b1000 -> new Command(GameCommandType.UP, new Object[0]);
            case 0b0100 -> new Command(GameCommandType.LEFT, new Object[0]);
            case 0b0001 -> new Command(GameCommandType.DOWN, new Object[0]);
            case 0b0010 -> new Command(GameCommandType.RIGHT, new Object[0]);
            case 0b1100 -> new Command(GameCommandType.TOP_LEFT, new Object[0]);
            case 0b1010 -> new Command(GameCommandType.TOP_RIGHT, new Object[0]);
            case 0b0101 -> new Command(GameCommandType.BOTTOM_LEFT, new Object[0]);
            case 0b0011 -> new Command(GameCommandType.BOTTOM_RIGHT, new Object[0]);
            default -> new Command(GameCommandType.CENTER, new Object[0]);
        };

        lastMoveDirection = Direction.valueOf(cmd.getCommandTypeInfo().getName().toUpperCase(Locale.ENGLISH));

        return cmd;
    }
}
