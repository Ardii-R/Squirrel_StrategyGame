package core.ui;

import core.board.BoardView;

public interface UI {

    Command getCommand();

    void updateCommand();

    void render(BoardView view);

    void message(String msg);
}
