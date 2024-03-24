package bg.sofia.uni.fmi.mjt.todoist.tsk.menu.parsers;

import bg.sofia.uni.fmi.mjt.todoist.tsk.menu.command.Command;
import bg.sofia.uni.fmi.mjt.todoist.tsk.menu.command.CommandField;

import java.util.Arrays;
import java.util.List;

public class StringToCommandParser {

    public static final String COMMAND_FIELD_SEPARATOR = "[ ][-]{2}";

    public static Command getCommand(String input) {
        String[] args = input.trim().split(COMMAND_FIELD_SEPARATOR);

        return new Command(args[0].trim(), getCommandFields(args));
    }
    private static List<CommandField> getCommandFields(String[] args) {
        return Arrays.stream(args).skip(1).map(String::trim).filter(StringToCommandFieldParser::isCommandField)
            .map(StringToCommandFieldParser::getCommandField).toList();
    }
}

