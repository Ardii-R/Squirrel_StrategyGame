package core.ui;

import java.util.regex.Pattern;

public interface CommandTypeInfo {

    String getName();

    String getHelpText();

    Pattern getRegex();

    int getParamsSize();
}
