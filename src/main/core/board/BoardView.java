package core.board;

import core.coordinates.XY;
import core.entity.EntityType;
import core.ui.gui.assets.Implosion;

import java.util.List;

public interface BoardView {

    EntityType getEntityTypeAt(int x, int y);

    List<Implosion> getImplosions();

    XY getSize();
}
