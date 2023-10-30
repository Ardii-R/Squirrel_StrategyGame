package core.entity.bot;

public class ImpactOutOfBoundsException  extends IndexOutOfBoundsException {
    public ImpactOutOfBoundsException() {
        super("Impact Radius out of bounds, must be between 2 and 10");
    };
}
