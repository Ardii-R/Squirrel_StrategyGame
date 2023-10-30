package core.entity.bot;

public class OutOfFovException extends IndexOutOfBoundsException {

    public OutOfFovException() {
        super("Point out of FOV");
    }
}
