package engine;

import Launch.Launcher;
import core.coordinates.Direction;
import core.coordinates.XY;
import core.entity.squirrel.NotEnoughEnergyException;
import core.ui.GameCommandType;
import core.ui.UI;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Game {

    private static final float FPS = 10;
    protected final State state;
    // Initialization to avoid NPE for first execution
    protected UI ui;

    protected Game(State state) {
        this.state = state;
    }

    public void run() {
        new Timer("GameThread").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                render();
                processInput();
                update();
            }
        }, 100, (long) (1000f / FPS));
        if (!Launcher.FX_MODE)
            new Timer("CommandThread").schedule(new TimerTask() {
                @Override
                public void run() {
                    while (true)
                        ui.updateCommand();
                }
            }, 100);
    }

    protected abstract void render();

    protected void processInput() {
        var command = ui.getCommand();
        if (command == null) return;
        Object[] params = command.getParams();
        GameCommandType commandType = (GameCommandType) command.getCommandTypeInfo();
        var classTypes = new Class<?>[params.length];
        for (var i = 0; i < params.length; i++)
            classTypes[i] = params[i].getClass();

        try {
            if (Arrays.stream(Direction.values())
                    .map(Enum::name)
                    .anyMatch(s -> s.equals(commandType.name()))) {
                getClass().getSuperclass().getDeclaredMethod("move", String.class).invoke(this, commandType.getName());
            } else {
                getClass().getSuperclass().getDeclaredMethod(commandType.getName(), classTypes).invoke(this, params);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void update() {
        try {
            state.update();
        } catch (NotEnoughEnergyException e) {
            ui.message("You do not have enough energy to do that!");
        }
    }

    private void help() {
        ui.message(
                Arrays.stream(GameCommandType.values())
                        .map(gameCommandType -> gameCommandType.getName() + "\n" + gameCommandType.getHelpText())
                        .collect(Collectors.joining("\n")));
    }

    private void exit() {
        ui.message("Goodbye!");
        System.exit(0);
    }

    private void log() {

    }

    private void master_energy() {
        ui.message("Energy: " + state.getCurrentEnergies());
    }

    private void spawn_mini(Integer energy, XY dir) {
        // do nothing
    }

    private void implode_mini(Integer id, Integer radius) {
        // do nothing
    }

    private void move(String dir) {
        // Do nothing
    }
}
