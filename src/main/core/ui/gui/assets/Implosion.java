package core.ui.gui.assets;

import core.coordinates.XY;
import javafx.scene.image.Image;

import java.util.List;

public class Implosion {

    private final XY start;
    private final int impactRadius;

    private final List<Image> frames;
    private int counter = 0;

    public Implosion(XY start, int impactRadius) {
        this.start = start;
        this.impactRadius = impactRadius;
        frames = loadFrames();
    }

    private List<Image> loadFrames() {
        return EntityAssets.IMPLOSION.loadFrames();
    }

    public boolean hasNext() {
        return counter < frames.size();
    }

    public Image getNext() {
        return frames.get(counter++);
    }

    public int getImpactRadius() {
        return impactRadius;
    }

    public XY getStart() {
        return start;
    }
}
