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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public enum StructureAssets {
    WALL;

    private final Map<Byte, Image> frameMap = new HashMap<>();

    StructureAssets() {
        loadFrames();
    }

    private void loadFrames() {
        URL resource = StructureAssets.class.getResource("/" + name().toLowerCase(Locale.ENGLISH));
        try (Stream<Path> stream = Files.walk(Paths.get(Objects.requireNonNull(resource).getPath().substring(Launcher.subString)))) {
            stream
                    .filter(path -> path.toFile().isFile() && path.toString().endsWith(".png"))
                    .forEach(path -> {
                        var name = path.getFileName().toString();
                        var b = Byte.parseByte(name.substring(0, name.lastIndexOf(".")), 2);

                        try (var is = new BufferedInputStream(new FileInputStream(path.toFile()))) {
                            frameMap.put(b, new Image(is));
                        } catch (IOException e) {
                            e.printStackTrace();
                            frameMap.put(b, null);
                        }
                    });
        } catch (IOException e) {
            // Ignored
        }
    }

    public Image getWallFrame(byte mask) {
        return frameMap.getOrDefault(mask, null);
    }
}
