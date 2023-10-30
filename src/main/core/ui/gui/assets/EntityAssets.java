package core.ui.gui.assets;

import Launch.Launcher;
import javafx.scene.image.Image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EntityAssets {
    BAD_BEAST,
    BAD_PLANT,
    GOOD_BEAST,
    GOOD_PLANT,
    GRASS,
    MASTER_SQUIRREL,
    MINI_SQUIRREL,
    IMPLOSION;

    private final List<Image> frames;
    private int counter = 0;

    EntityAssets() {
        this.frames = loadFrames();
    }

    public List<Image> loadFrames() {
        URL resource = getClass().getResource("/" + name().toLowerCase(Locale.ENGLISH));
        try (Stream<Path> stream = Files.walk(Paths.get(Objects.requireNonNull(resource).getPath().substring(Launcher.subString)))) {
            return stream
                    .filter(path -> path.toFile().isFile() && path.toString().endsWith(".png"))
                    .map(path -> {
                        try (var is = new BufferedInputStream(new FileInputStream(path.toFile()))) {
                            return new Image(is);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public Image getNextFrame() {
        return frames.get(counter = ++counter % frames.size());
    }
}
