package engine;

import core.ui.console.ConsoleUI;

public class ConsoleGame extends Game {

    public ConsoleGame(State state) {
        super(state);
        ui = new ConsoleUI();
    }

    @Override
    protected void render() {
        ui.render(state.flattenedBoard());
        System.out.println("Energy: " + state.getCurrentEnergies() + " | Highscore: " + state.getHighScoreMessage());
    }
}