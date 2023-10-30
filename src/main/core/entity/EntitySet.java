package core.entity;

import core.coordinates.XY;
import core.entity.bot.ImpactOutOfBoundsException;
import core.entity.bot.OutOfFovException;
import core.entity.squirrel.MasterSquirrel;
import core.entity.squirrel.MiniSquirrel;
import core.entity.squirrel.NotEnoughEnergyException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EntitySet implements Iterable<Entity> {

    private final Entity[] entities;
    private static final Logger logger = Logger.getLogger(EntitySet.class.getName());

    public EntitySet(int maxSize) {
        this.entities = new Entity[maxSize];
    }

    public void add(Entity entity) {
        var i = 0;
        while (i < entities.length && entities[i] != null)
            i++;
        entities[i] = entity;
        logger.info("Adding entity " + entity);
    }

    public void addAll(Entity... entities) {
        for(Entity entity : entities)
            add(entity);
    }

    public Entity[] getEntityArray() {
        return entities;
    }

    public boolean contains(Entity entity) {
        return Arrays.asList(entities).contains(entity);
    }

    public boolean remove(Entity entity) {
        for (var i = 0; i < entities.length; i++) {
            if (entities[i] != null && entities[i].equals(entity)) {
                entities[i] = null;
                logger.info("Removing entity " + entity);
                return true;
            }
        }
        logger.info("Entity " + entity + " not found");
        return false;
    }

    public void nextStep(EntityContext context) throws NotEnoughEnergyException{
        for (Entity entity : entities) {
            if(entity instanceof Character)
                ((Character) entity).nextStep(context);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(Arrays.stream(entities)
                .map(Entity::toString)
                .collect(Collectors.joining(",\n")));
    }

    @Override
    public Iterator<Entity> iterator() {
        return new Iterator<>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                int i = index;
                while (entities[i++] == null) {
                    if (i >= entities.length)
                        return false;
                }
                return true;
            }

            @Override
            public Entity next() {
                while (entities[index] == null)
                    index++;
                return entities[index++];
            }
        };
    }
}
