package bg.sofia.uni.fmi.mjt.todoist.menu.printers;

import bg.sofia.uni.fmi.mjt.todoist.database.adapter.LocalDateAdapter;
import bg.sofia.uni.fmi.mjt.todoist.task.GeneralTask;
import com.google.gson.Gson;

public class GeneralTaskPrinter {
    private static final Gson GSON = LocalDateAdapter.GSON;

    public static String printGeneralTask(GeneralTask task) {
        return GSON.toJson(task);
    }
}
