package bg.sofia.uni.fmi.mjt.todoist.menu.executor;

import bg.sofia.uni.fmi.mjt.todoist.database.Database;
import bg.sofia.uni.fmi.mjt.todoist.menu.command.Command;
import bg.sofia.uni.fmi.mjt.todoist.menu.command.CommandField;
import bg.sofia.uni.fmi.mjt.todoist.menu.executor.literals.CommandExecutorFields;
import bg.sofia.uni.fmi.mjt.todoist.menu.executor.literals.CommandExecutorResponses;
import bg.sofia.uni.fmi.mjt.todoist.menu.printers.GeneralTaskPrinter;
import bg.sofia.uni.fmi.mjt.todoist.task.GeneralTask;
import bg.sofia.uni.fmi.mjt.todoist.task.TaskKey;
import bg.sofia.uni.fmi.mjt.todoist.user.Account;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandExecutor {
    private static final String ADD_TASK = "add-task";
    private static final String UPDATE_TASK = "update-task";
    private static final String DELETE_TASK = "delete-task";
    private static final String GET_TASK = "get-task";
    private static final String LIST_TASKS = "list-tasks";
    private static final String LIST_DASHBOARD = "list-dashboard";
    private static final String FINISH_TASK = "finish-task";
    private static final String LOGOUT = "logout";

    protected static final String LOCAL_DATE_FORMAT = "dd/MM/yyyy";

    protected Account userAccount;

    protected Database database;
    protected String message;

    public CommandExecutor(Account userAccount, Database database) {
        this.userAccount = userAccount;
        this.database = database;

        setEmptyMessage();
    }

    public String execute(Command cmd) {
        Map<String, String> mapFields = getMapFields(cmd.fields());

        return switch (cmd.commandName()) {
            case LOGOUT -> CommandExecutorResponses.LOG_OUT.getDescription();

            case ADD_TASK -> addTask(mapFields);
            case UPDATE_TASK -> updateTask(mapFields);
            case DELETE_TASK -> deleteTask(mapFields);
            case GET_TASK -> getTask(mapFields);
            case FINISH_TASK -> finishTask(mapFields);
            case LIST_DASHBOARD -> listDashboard();
            case LIST_TASKS -> listTasks(mapFields);

            default -> CommandExecutorResponses.UNKNOWN_COMMAND.getDescription();
        };
    }

    protected String getUsername() {
        return userAccount.getUsername();
    }

    protected void setEmptyMessage() {
        message = null;
    }

    protected boolean checkIsEmptyMessage() {
        return message == null;
    }

    protected Map<String, String> getMapFields(List<CommandField> fields) {
        return fields.stream().collect(Collectors.toMap(CommandField::fieldName, CommandField::value));
    }

    protected LocalDate getLocalDate(String dateString) {
        LocalDate localDate = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT);

        if (dateString != null) {
            localDate = LocalDate.parse(dateString, formatter);
        }

        return localDate;
    }

    protected GeneralTask getGeneralTask(Map<String, String> mapFields) {
        setEmptyMessage();

        if (!CommandExecutorFields.checkCommandMapHasName(mapFields)) {
            message = CommandExecutorResponses.NO_TASK_NAME_SPECIFIED.getDescription();
            return null;
        }

        String name = CommandExecutorFields.getField(mapFields, CommandExecutorFields.NAME);
        String date = CommandExecutorFields.getField(mapFields, CommandExecutorFields.DATE);
        String dueDate = CommandExecutorFields.getField(mapFields, CommandExecutorFields.DUE_DATE);
        String description = CommandExecutorFields.getField(mapFields, CommandExecutorFields.DESCRIPTION);

        GeneralTask generalTask;
        try {
            generalTask =
                new GeneralTask.TaskBuilder(name).setDate(getLocalDate(date)).setDueDate(getLocalDate(dueDate))
                    .setDescription(description).build();
        } catch (Exception e) {
            message = CommandExecutorResponses.INCORRECT_DATE_FORMAT.getDescription();
            return null;
        }

        return generalTask;
    }

    private String addTask(Map<String, String> mapFields) {
        GeneralTask generalTask = getGeneralTask(mapFields);

        if (generalTask == null) {
            return message;
        }

        if (userAccount.addTask(generalTask)) {
            database.saveOnlyTasksOfUser(getUsername());

            return CommandExecutorResponses.TASK_ADDED_SUCCESSFULLY.getDescription();
        }
        return CommandExecutorResponses.TASK_ADDITION_FAILED.getDescription();
    }

    private String updateTask(Map<String, String> mapFields) {
        GeneralTask generalTask = getGeneralTask(mapFields);

        if (generalTask == null) {
            return message;
        }

        GeneralTask toBeUpdated = userAccount.getTask(TaskKey.of(generalTask));

        if (generalTask.getDueData() != null) {
            toBeUpdated.setDueData(generalTask.getDueData());
        }

        if (generalTask.getDescription() != null) {
            toBeUpdated.setDescription(generalTask.getDescription());
        }

        database.saveOnlyTasksOfUser(getUsername());

        return CommandExecutorResponses.TASK_UPDATED_SUCCESSFULLY.getDescription();
    }

    protected TaskKey getTaskKey(Map<String, String> mapFields) {
        setEmptyMessage();

        if (!CommandExecutorFields.checkCommandMapHasName(mapFields)) {
            message = CommandExecutorResponses.NO_TASK_NAME_SPECIFIED.getDescription();
            return null;
        }

        String name = CommandExecutorFields.getField(mapFields, CommandExecutorFields.NAME);
        String date = CommandExecutorFields.getField(mapFields, CommandExecutorFields.DATE);

        TaskKey taskKey;
        try {
            taskKey = new TaskKey(name, getLocalDate(date));
        } catch (Exception e) {
            message = CommandExecutorResponses.INCORRECT_DATE_FORMAT.getDescription();
            return null;
        }

        return taskKey;
    }

    private String deleteTask(Map<String, String> mapFields) {

        TaskKey taskKey = getTaskKey(mapFields);
        if (taskKey == null || !checkIsEmptyMessage()) {
            return message;
        }

        if (userAccount.deleteTask(taskKey)) {
            database.saveOnlyTasksOfUser(getUsername());
            return CommandExecutorResponses.TASK_DELETED_SUCCESSFULLY.getDescription();
        }
        return CommandExecutorResponses.TASK_DELETION_FAILED.getDescription();
    }

    protected String concat(String response, List<String> results) {
        StringBuilder sb = new StringBuilder().append(response);

        results.forEach(str -> sb.append(System.lineSeparator()).append(str));

        return sb.toString();
    }

    private String getTask(Map<String, String> mapFields) {
        TaskKey taskKey = getTaskKey(mapFields);
        if (taskKey == null || !checkIsEmptyMessage()) {
            return message;
        }

        GeneralTask task = userAccount.getTask(taskKey);

        if (task == null) {
            return CommandExecutorResponses.TASK_NOT_FOUND.getDescription();
        }

        return concat(CommandExecutorResponses.TASK_FOUND_SUCCESSFULLY.getDescription(),
            Collections.singletonList(GeneralTaskPrinter.printGeneralTask(task)));
    }

    private String finishTask(Map<String, String> mapFields) {
        TaskKey taskKey = getTaskKey(mapFields);
        if (taskKey == null || !checkIsEmptyMessage()) {
            return message;
        }

        if (userAccount.setTaskAsFinished(taskKey)) {
            database.saveOnlyTasksOfUser(getUsername());
            return CommandExecutorResponses.TASK_FINISHED_SUCCESSFULLY.getDescription();
        }
        return CommandExecutorResponses.TASK_NOT_FOUND.getDescription();
    }

    private String listDashboard() {
        List<GeneralTask> tasks = userAccount.getTasksForToday();

        if (tasks.isEmpty()) {
            return CommandExecutorResponses.NO_TASKS_FOR_TODAY.getDescription();
        }

        return concat(CommandExecutorResponses.PRINTING_TASKS_FOR_TODAY.getDescription(), getAsListOfStrings(tasks));
    }

    protected List<String> getAsListOfStrings(List<GeneralTask> tasks) {
        return tasks.stream().map(GeneralTaskPrinter::printGeneralTask).toList();
    }

    private List<GeneralTask> chooseHowToGetTasks(String completed, String date, Boolean completedBoolean,
                                                  LocalDate localDate) {
        if (completed == null && date == null) {
            return userAccount.getTasksNoDate();
        }
        if (completed == null && date != null) {
            return userAccount.getTasksForDate(localDate);
        }
        if (completed != null && date == null) {
            return userAccount.getTasksNoDateWithState(completedBoolean);
        }

        return userAccount.getTasksForDateWithState(localDate, completedBoolean);
    }

    private String listTasks(Map<String, String> mapFields) {
        List<GeneralTask> tasks;

        String completed = CommandExecutorFields.getField(mapFields, CommandExecutorFields.COMPLETED);
        String date = CommandExecutorFields.getField(mapFields, CommandExecutorFields.DATE);

        boolean completedBoolean = Boolean.parseBoolean(completed);

        LocalDate localDate;
        try {
            localDate = getLocalDate(date);
        } catch (Exception e) {
            return CommandExecutorResponses.INCORRECT_DATE_FORMAT.getDescription();
        }

        tasks = chooseHowToGetTasks(completed, date, completedBoolean, localDate);

        if (tasks.isEmpty()) {
            return CommandExecutorResponses.NO_TASKS.getDescription();
        }

        return concat(CommandExecutorResponses.PRINTING_TASKS.getDescription(), getAsListOfStrings(tasks));
    }
}
