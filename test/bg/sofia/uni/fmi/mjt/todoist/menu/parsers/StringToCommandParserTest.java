package bg.sofia.uni.fmi.mjt.todoist.menu.parsers;

import bg.sofia.uni.fmi.mjt.todoist.menu.command.Command;
import bg.sofia.uni.fmi.mjt.todoist.menu.command.CommandField;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringToCommandParserTest {
    @Test
    void testGetCommandOneWord() {
        Command command = new Command("command", new ArrayList<>());

        String input = "command";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandOneWordSpacesBefore() {
        Command command = new Command("command", new ArrayList<>());

        String input = "     command";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandOneWordSpacesAfter() {
        Command command = new Command("command", new ArrayList<>());

        String input = "command      ";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandOneWordSpacesBeforeAndAfter() {
        Command command = new Command("command", new ArrayList<>());

        String input = "     command      ";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandOneLetter() {
        Command command = new Command("c", new ArrayList<>());

        String input = "c";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandOneDigit() {
        Command command = new Command("1", new ArrayList<>());

        String input = "1";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandOneDigitWithField() {
        List<CommandField> list = List.of(new CommandField("name", "value"));
        Command command = new Command("1", list);

        String input = "1 --name=value";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandCommandNameOnlyFieldKey() {
        Command command = new Command("command", List.of());

        String input = "command --name";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandOnlyFieldKey() {
        Command command = new Command("--name", List.of());

        String input = "--name";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandOnlyDoubleFieldKey() {
        Command command = new Command("--name", List.of());

        String input = "--name --name";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandCommandNameOnlyFieldKeyEqual() {
        Command command = new Command("command", List.of());

        String input = "command --name=";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandCommandNameOnlyEqual() {
        Command command = new Command("command =", List.of());

        String input = "command =";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandCommandNameOnlyEqualValue() {
        Command command = new Command("command =value", List.of());

        String input = "command =value";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandCommandNameNoFieldSeparator() {
        Command command = new Command("command name=value", List.of());

        String input = "command name=value";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandTwoFields() {
        List<CommandField> list = List.of(new CommandField("name", "value"), new CommandField("years", "value2"));

        Command command = new Command("command", list);

        String input = "command --name=value --years=value2";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandTwoSameFields() {
        List<CommandField> list = List.of(new CommandField("name", "value"), new CommandField("name", "value"));

        Command command = new Command("command", list);

        String input = "command --name=value --name=value";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandMultipleFieldSeparator() {
        List<CommandField> list = List.of(new CommandField("name", "=value"));

        Command command = new Command("command", list);

        String input = "command --name==value";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandEmpty() {
        Command command = new Command("", List.of());

        String input = "";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

    @Test
    void testGetCommandBlank() {
        Command command = new Command("", List.of());

        String input = "  ";

        assertEquals(command, StringToCommandParser.getCommand(input), "Expected command to be parsed correctly");
    }

}
