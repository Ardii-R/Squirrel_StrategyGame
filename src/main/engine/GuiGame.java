package engine;

import core.ui.UI;

public class GuiGame extends Game {

    public GuiGame(State state, UI ui) {
        super(state);
        this.ui = ui;
    }

    @Override
    protected void render() {
        ui.render(state.flattenedBoard());
        ui.message("Energy: " + state.getCurrentEnergies() + " | Highscore: " + state.getHighScoreMessage());
    }
}
