package core.entity.squirrel;

public class NotEnoughEnergyException extends Exception {

    public NotEnoughEnergyException() {
        super("Not enough energy!");
    }
}
