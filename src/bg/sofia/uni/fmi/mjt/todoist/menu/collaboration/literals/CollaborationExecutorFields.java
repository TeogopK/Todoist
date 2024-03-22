package bg.sofia.uni.fmi.mjt.todoist.menu.collaboration.literals;

import java.util.Map;

public enum CollaborationExecutorFields {
    COLLABORATION("collaboration"), USER("user");

    private final String description;

    CollaborationExecutorFields(String description) {
        this.description = description;
    }

    public static String getField(Map<String, String> map, CollaborationExecutorFields executorField) {
        return map.get(executorField.description);
    }
}
