package bg.sofia.uni.fmi.mjt.todoist.menu.printers;

import bg.sofia.uni.fmi.mjt.todoist.database.adapter.LocalDateAdapter;
import bg.sofia.uni.fmi.mjt.todoist.task.CollaborationTask;
import com.google.gson.Gson;

public class CollaborationTaskPrinter {
    private static final Gson GSON = LocalDateAdapter.GSON;

    public static String printCollaborationTask(CollaborationTask task) {
        return GSON.toJson(task);
    }

}
