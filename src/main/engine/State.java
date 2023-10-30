package engine;

import botapi.CommandReceiver;
import core.board.Board;
import core.board.FlattenedBoard;
import core.coordinates.XY;
import core.entity.Character;
import core.entity.Entity;
import core.entity.EntityContext;
import core.entity.bot.MasterSquirrelBot;
import core.entity.bot.factory.handoperated.Handoperated;
import core.entity.squirrel.MasterSquirrel;
import core.entity.squirrel.MiniSquirrel;
import core.entity.squirrel.NotEnoughEnergyException;

import javax.swing.event.ChangeListener;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Optional;

public class State {

    private final Board board;
    private int highScore;
    private MasterSquirrelBot best;
    private String message;

    public State(Board board) {
        this.board = board;
    }

    public void update() throws NotEnoughEnergyException {
        message = "";
        board.getEntities().nextStep(flattenedBoard());
        for (Entity entity : board.getEntities()) {
            if (entity instanceof MasterSquirrel){
                if(highScore < entity.getEnergy()) {
                    highScore = entity.getEnergy();
                    best = (MasterSquirrelBot) entity;
                }
                message += ((MasterSquirrelBot)entity).getFactory().getClass().getSimpleName() +
                        ": " +
                        entity.getEnergy() +
                        " | ";
            }
        }
    }

    protected String getHighScoreMessage() {
        if(best == null)
            return "0";
        return String.format("%d (%s)", highScore, best.getFactory().getClass().getSimpleName());
    }

    protected String getCurrentEnergies() {
        return message;
    }

    public FlattenedBoard flattenedBoard() {
        return board.flatten();
    }
}
