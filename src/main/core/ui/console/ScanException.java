package core.ui.console;

import java.util.InputMismatchException;

public final class ScanException extends InputMismatchException {

    public ScanException(String s) {
        super(s);
    }
}
