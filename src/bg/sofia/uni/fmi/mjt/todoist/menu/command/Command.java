package bg.sofia.uni.fmi.mjt.todoist.menu.command;

import java.util.List;

public record Command(String commandName, List<CommandField> fields) {
}
