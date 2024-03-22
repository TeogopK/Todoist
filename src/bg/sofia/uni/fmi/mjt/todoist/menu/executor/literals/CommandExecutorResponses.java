package bg.sofia.uni.fmi.mjt.todoist.menu.executor.literals;

public enum CommandExecutorResponses {
    NO_TASK_NAME_SPECIFIED("No task name was specified!"),

    TASK_ADDED_SUCCESSFULLY("Task added successfully!"),

    TASK_ADDITION_FAILED(
        "Task addition failed! Check if the task with the specified name (and date) already exists!"),

    TASK_DELETED_SUCCESSFULLY("Task deleted successfully!"),

    TASK_FINISHED_SUCCESSFULLY("Task was finished successfully!"),

    TASK_UPDATED_SUCCESSFULLY("Task was updated successfully!"),

    TASK_DELETION_FAILED("Task deletion failed! Check if task with specified name (and date) exists!"),

    NO_TASKS_FOR_TODAY("There are no tasks for today!"),
    PRINTING_TASKS_FOR_TODAY("Printing tasks for today: "),

    NO_TASKS("There are no tasks with the specified parameters!"),
    PRINTING_TASKS("Printing tasks with the specified parameters: "),

    INCORRECT_DATE_FORMAT("Command failed! Incorrect date format! Example format: 20/10/2002"),

    TASK_NOT_FOUND("Could not find task with specified name (and date)!"),

    TASK_FOUND_SUCCESSFULLY("Task found successfully! Printing info for task: "),

    UNKNOWN_COMMAND("Unknown executor command!"),

    LOG_OUT("Logging out successful!");

    private final String message;

    CommandExecutorResponses(String message) {
        this.message = message;
    }

    public String getDescription() {
        return message;
    }
}
