package bg.sofia.uni.fmi.mjt.todoist.tsk.menu.printers;

import bg.sofia.uni.fmi.mjt.todoist.tsk.database.adapter.LocalDateAdapter;
import bg.sofia.uni.fmi.mjt.todoist.tsk.task.GeneralTask;
import com.google.gson.Gson;

public class GeneralTaskPrinter {
    private static final Gson GSON = LocalDateAdapter.GSON;

    public static String printGeneralTask(GeneralTask task) {
        return GSON.toJson(task);
    }
}
