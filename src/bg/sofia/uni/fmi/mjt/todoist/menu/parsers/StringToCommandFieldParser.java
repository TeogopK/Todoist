package bg.sofia.uni.fmi.mjt.todoist.menu.parsers;

import bg.sofia.uni.fmi.mjt.todoist.menu.command.CommandField;

public class StringToCommandFieldParser {

    public static final String COMMAND_FIELD_VALUE_SEPARATOR = "=";
    public static final int ARGUMENT_SIZE = 2;

    public static CommandField getCommandField(String line) {
        String[] args = line.trim().split(COMMAND_FIELD_VALUE_SEPARATOR, ARGUMENT_SIZE);

        return new CommandField(args[0], args[1]);
    }

    public static boolean isCommandField(String line) {
        int index = line.indexOf(COMMAND_FIELD_VALUE_SEPARATOR);

        return line.contains(COMMAND_FIELD_VALUE_SEPARATOR) && index > 0 && index < line.length() - 1;
    }
}
