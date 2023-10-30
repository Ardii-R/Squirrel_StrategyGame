package core.board;

import botapi.BotControllerFactory;
import core.coordinates.XY;
import core.coordinates.XYsupport;
import core.entity.*;
import core.entity.bot.MasterSquirrelBot;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static java.lang.String.format;

public final class Board {

    private final EntitySet entities;
    private final int length;
    private final int width;
    private int id = 0;
    private static final Logger logger = Logger.getLogger(Board.class.getName());

    public Board(BoardConfig config, BotControllerFactory... factories) {
        this.length = config.getLength();
        this.width = config.getWidth();
        this.entities = new EntitySet(length * width);

        logger.info("Initializing Board:\t-width: " + width + "\t-length: " + length);
        logger.info("Adding walls");
        addWalls();
        logger.info("Adding entities");
        addEntities(config, factories);
    }

    public EntitySet getEntities() {
        return entities;
    }

    private void addWalls() {
        for (var y = 0; y < width; y++) {
            if (y == 0 || y == width - 1) {
                // Top and bottom walls
                for (var x = 0; x < length; x++) {
                    entities.add(new Wall(getNextId(), new XY(x, y)));
                }
            } else {
                // Add walls at the border but leave the middle
                entities.add(new Wall(getNextId(), new XY(0, y)));
                entities.add(new Wall(getNextId(), new XY(length - 1, y)));
            }
        }
    }

    private void addEntities(BoardConfig config, BotControllerFactory... factories) {
        Set<XY> occupiedCoords = new HashSet<>();

        config.getAttributes().forEach((attribute, value) -> {
            if (attribute != BoardConfig.Attributes.LENGTH && attribute != BoardConfig.Attributes.WIDTH) {
                for (var i = 0; i < value; i++) {
                    var xy = XYsupport.getRandomPosition(length - 2, width - 2);
                    // Reassign until xy is not occupied and is not placed on the border of the board
                    while (occupiedCoords.contains(xy)
                            || xy.getX() == 0 || xy.getX() == length - 1
                            || xy.getY() == 0 || xy.getY() == width - 1)
                        xy = XYsupport.getRandomPosition(length - 2, width - 2);
                    Entity entity = switch (attribute) {
                        case GOOD_BEAST_COUNT -> new GoodBeast(getNextId(), xy);
                        case GOOD_PLANT_COUNT -> new GoodPlant(getNextId(), xy);
                        case BAD_BEAST_COUNT -> new BadBeast(getNextId(), xy);
                        case BAD_PLANT_COUNT -> new BadPlant(getNextId(), xy);
                        case WALL_COUNT -> new Wall(getNextId(), xy);
                        default -> null;
                    };
                    if(entity != null)
                        entities.add(entity);
                    occupiedCoords.add(xy);
                }
            }
        });
        addBots(occupiedCoords, factories);
    }

    private void addBots(Set<XY> occupiedCoords, BotControllerFactory... botControllerFactories) {
        for(BotControllerFactory factory : botControllerFactories) {
            var squirrelPos = XYsupport.getRandomPosition(length - 2, width - 2);
            while (squirrelPos.getX() == 0 || squirrelPos.getY() == 0 || occupiedCoords.contains(squirrelPos))
                squirrelPos = XYsupport.getRandomPosition(length - 2, width - 2);

            occupiedCoords.add(squirrelPos);
            entities.add(new MasterSquirrelBot(getNextId(), squirrelPos, factory));
        }
    }

    public int getNextId() {
        return id++;
    }

    public FlattenedBoard flatten() {
        return new FlattenedBoard(this, length, width);
    }


    @Override
    public String toString() {
        return format("Board: %sx%s%nNumber Of Entities: %s", length, width, id);
    }
}
