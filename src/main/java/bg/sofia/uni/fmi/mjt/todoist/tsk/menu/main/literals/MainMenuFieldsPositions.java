package bg.sofia.uni.fmi.mjt.todoist.tsk.menu.main.literals;

import bg.sofia.uni.fmi.mjt.todoist.tsk.menu.command.CommandField;

import java.util.List;

public enum MainMenuFieldsPositions {
    USERNAME("username", 0), PASSWORD("password", 1);

    private final String description;

    private final Integer position;

    MainMenuFieldsPositions(String description, Integer position) {
        this.description = description;
        this.position = position;
    }

    public Integer getPosition() {
        return position;
    }

    public static boolean checkCommandField(List<CommandField> list) {
        if (list.size() != values().length) {
            return false;
        }

        for (var value : values()) {
            if (!list.get(value.position).fieldName().equals(value.description)) {
                return false;
            }
        }
        return true;
    }

    public static String getField(List<CommandField> list, MainMenuFieldsPositions fieldsPositions) {
        return list.get(fieldsPositions.getPosition()).value();
    }
}