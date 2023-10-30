package Launch;

import botapi.BotControllerFactory;
import botapi.CommandReceiver;
import core.board.Board;
import core.board.BoardConfig;
import core.entity.bot.factory.challenge.ArdiMaxisBot;
import core.entity.bot.factory.handoperated.HandOperatedSquirrel;
import core.ui.UI;
import core.ui.gui.FxUI;
import core.ui.gui.assets.EntityAssets;
import engine.ConsoleGame;
import engine.Game;
import engine.GuiGame;
import engine.State;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Launcher extends Application {

    public static final boolean FX_MODE = true;
    public static final boolean HAND_OPERATED = false;
    public static final int subString = System.getProperty("os.name").equals("Mac OS X")
            ? 0
            : 1;
    private static final Logger logger = Logger.getLogger(Launcher.class.getName());
    private static final LogManager logManager = LogManager.getLogManager();

    public static void main(String[] args) {
        try {
            logManager.readConfiguration(new FileInputStream("logging.properties")); // Reads and initializes the logging configuration from the given input stream
        } catch(IOException e) {
            logger.warning(e.getMessage());
        }

        logger.info("Squirrel hand-operated: " + HAND_OPERATED);

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        BotControllerFactory[] factories = new BotControllerFactory[] {
                new HandOperatedSquirrel(),
                //new RandomBot(),
                new ArdiMaxisBot(),
                //new MertsBot()
        };

        List<CommandReceiver> receivers = new ArrayList<>();
        for (BotControllerFactory factory : factories) {
            if (factory instanceof CommandReceiver) {
                receivers.add((CommandReceiver) factory);
            }
        }

        var state = new State(new Board(new BoardConfig(), factories));

        final Game game;
        final UI ui;

        if(FX_MODE) {
            logger.info("starting in fx Mode");
            ui = FxUI.createInstance(state.flattenedBoard().getSize(), receivers.toArray(new CommandReceiver[0]));
            game = new GuiGame(state, ui);

            primaryStage.setTitle("DiligentSquirrel");
            primaryStage.sizeToScene();
            primaryStage.getIcons().add(EntityAssets.MASTER_SQUIRREL.getNextFrame());
            primaryStage.setResizable(false);
            primaryStage.setScene((Scene) ui);
            primaryStage.show();

            ((Scene)ui).getWindow().setOnCloseRequest(event -> System.exit(0));
        } else {
            logger.info("starting in console mode");
            logger.warning("too fast for console input");
            game = new ConsoleGame(new State(new Board(new BoardConfig(), factories)));
        }

        game.run();
    }


    public boolean getHAND_OPERATED(){
        return HAND_OPERATED;
    }
}
