package core.ui.console;

import core.coordinates.Direction;
import core.ui.Command;
import core.ui.CommandTypeInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

public class CommandScanner {

    private static final Pattern INT_REGEX = Pattern.compile("\\d+");
    private final CommandTypeInfo[] commandTypeInfos;
    private final BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

    public CommandScanner(CommandTypeInfo[] commandTypeInfos) {
        this.commandTypeInfos = commandTypeInfos;
    }

    public Command next() throws ScanException {
        try {
            var inputString = inputReader.readLine().strip();
            String[] input = inputString.split("\s+");
            return Arrays.stream(commandTypeInfos)
                    .filter(commandTypeInfo -> commandTypeInfo.getRegex().matcher(inputString).matches())
                    .map(commandTypeInfo -> {
                        var params = new Object[commandTypeInfo.getParamsSize()];
                        String[] stringParams = Arrays.copyOfRange(input, 1, input.length);

                        for (var i = 0; i < stringParams.length; i++) {
                            if (INT_REGEX.matcher(stringParams[i]).matches())
                                params[i] = Integer.parseInt(stringParams[i]);
                            else {
                                String direction = stringParams[i].toUpperCase(Locale.ENGLISH);
                                if (Arrays.stream(Direction.values())
                                        .map(Enum::name)
                                        .anyMatch(s -> s.equals(direction)))
                                    params[i] = Arrays.stream(Direction.class.getEnumConstants())
                                            .filter(constant -> constant.name().equals(direction))
                                            .findAny()
                                            .orElse(Direction.CENTER)
                                            .getVector();
                            }
                        }

                        return new Command(commandTypeInfo, params);
                    })
                    .findAny()
                    .orElseThrow(() -> new ScanException("Unknown command!"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
