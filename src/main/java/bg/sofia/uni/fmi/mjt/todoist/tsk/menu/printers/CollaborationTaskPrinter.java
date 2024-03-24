package bg.sofia.uni.fmi.mjt.todoist.tsk.menu.printers;

import bg.sofia.uni.fmi.mjt.todoist.tsk.database.adapter.LocalDateAdapter;
import bg.sofia.uni.fmi.mjt.todoist.tsk.task.CollaborationTask;
import com.google.gson.Gson;

public class CollaborationTaskPrinter {
    private static final Gson GSON = LocalDateAdapter.GSON;

    public static String printCollaborationTask(CollaborationTask task) {
        return GSON.toJson(task);
    }

}
