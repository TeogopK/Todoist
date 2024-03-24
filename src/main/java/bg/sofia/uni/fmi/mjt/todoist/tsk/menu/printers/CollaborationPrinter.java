package bg.sofia.uni.fmi.mjt.todoist.tsk.menu.printers;

import bg.sofia.uni.fmi.mjt.todoist.tsk.database.serializers.CollaborationEntrySerializer;
import bg.sofia.uni.fmi.mjt.todoist.tsk.task.collaboration.Collaboration;
import com.google.gson.Gson;

public class CollaborationPrinter {

    private static final Gson GSON = new Gson();

    public static String getNameAndCreator(Collaboration collaboration) {
        return GSON.toJson(CollaborationEntrySerializer.of(collaboration));
    }
}
