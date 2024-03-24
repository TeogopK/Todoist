package bg.sofia.uni.fmi.mjt.todoist.tsk.menu.executor.literals;

import java.util.Map;

public enum CommandExecutorFields {
    NAME("name"), DUE_DATE("due-date"),
    DATE("date"), DESCRIPTION("description"), COMPLETED("completed");

    private final String description;

    CommandExecutorFields(String description) {
        this.description = description;
    }

    public static boolean checkCommandMapHasName(Map<String, String> map) {
        return getField(map, NAME) != null;
    }

    public static String getField(Map<String, String> map, CommandExecutorFields executorField) {
        return map.get(executorField.description);
    }
}
