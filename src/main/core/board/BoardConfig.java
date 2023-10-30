package core.board;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

public final class BoardConfig {

    private final Map<Attributes, Integer> attributesMap = new EnumMap<>(Attributes.class);

    public BoardConfig() {
        parseConfig();
    }

    private void parseConfig() {
        try (var is = Files.newInputStream(Paths.get("board_config.properties"))) {
            var properties = new Properties();
            properties.load(is);
            Arrays.stream(Attributes.values())
                    .map(Enum::name)
                    .forEach(attribute -> {
                        if (properties.getProperty(attribute) != null)
                            try {
                                var attributes = Attributes.valueOf(attribute);
                                var property = Integer.parseInt(properties.getProperty(attribute));
                                if (!attribute.equals("WALL_COUNT") && property <= 0) {
                                    System.err.println("Invalid property value for " + attribute + ": Lower or equal to zero.");
                                    property = attributes.defaultValue;
                                }
                                attributesMap.put(attributes, property);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid property value for " + attribute + ": Not a number.");
                                var attributes = Attributes.valueOf(attribute);
                                attributesMap.put(attributes, attributes.defaultValue);
                            } catch (IllegalArgumentException e) {
                                System.err.println("Unknown property " + attribute + ".");
                            }
                        else {
                            System.err.println("Property " + attribute + " not found, using default value!");
                            var attributes = Attributes.valueOf(attribute);
                            attributesMap.put(attributes, attributes.defaultValue);
                        }
                    });
        } catch (IOException e) {
            System.err.println("Properties File not found, using default values!");
            for (Attributes attribute : Attributes.values())
                attributesMap.put(attribute, attribute.defaultValue);
        }
        validateBoardSize();
    }

    private void validateBoardSize() {
        // Check if current board config allows for specified amount of entities
        final var entities = new int[]{0};
        attributesMap.forEach((k, v) -> {
            if (k == Attributes.BAD_BEAST_COUNT || k == Attributes.BAD_PLANT_COUNT
                    || k == Attributes.GOOD_BEAST_COUNT || k == Attributes.GOOD_PLANT_COUNT
                    || k == Attributes.WALL_COUNT)
                entities[0] += v;
        });
        // Keep increasing field until all entities fit
        for (int size = Math.max(attributesMap.get(Attributes.LENGTH), attributesMap.get(Attributes.WIDTH));
             attributesMap.get(Attributes.LENGTH) * attributesMap.get(Attributes.WIDTH) <= entities[0];
             size++) {
            attributesMap.put(Attributes.LENGTH, size);
            attributesMap.put(Attributes.WIDTH, size);
        }
    }

    public Map<Attributes, Integer> getAttributes() {
        return attributesMap;
    }

    public int getLength() {
        return attributesMap.getOrDefault(Attributes.LENGTH, Attributes.LENGTH.defaultValue);
    }

    public int getWidth() {
        return attributesMap.getOrDefault(Attributes.WIDTH, Attributes.WIDTH.defaultValue);
    }

    protected enum Attributes {
        LENGTH(15),
        WIDTH(15),
        BAD_BEAST_COUNT(3),
        BAD_PLANT_COUNT(5),
        GOOD_BEAST_COUNT(5),
        GOOD_PLANT_COUNT(7),
        WALL_COUNT(10);

        private final int defaultValue;

        Attributes(int defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}