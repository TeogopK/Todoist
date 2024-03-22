package bg.sofia.uni.fmi.mjt.todoist.database.serializers;

import bg.sofia.uni.fmi.mjt.todoist.task.collaboration.Collaboration;

import java.util.Map;

public record CollaborationEntrySerializer(String collaborationName, String adminName) {
    public static CollaborationEntrySerializer of(Map.Entry<String, String> entry) {
        return new CollaborationEntrySerializer(entry.getKey(), entry.getValue());
    }

    public static CollaborationEntrySerializer of(Collaboration collaboration) {
        return new CollaborationEntrySerializer(collaboration.getCollaborationName(), collaboration.getCreatorName());
    }
}