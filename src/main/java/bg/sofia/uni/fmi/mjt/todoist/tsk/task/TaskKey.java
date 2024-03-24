package bg.sofia.uni.fmi.mjt.todoist.tsk.task;

import java.time.LocalDate;

public record TaskKey(String taskName, LocalDate taskDate) {
    public static TaskKey of(AbstractTask task) {
        return new TaskKey(task.name, task.date);
    }
}


