package bg.sofia.uni.fmi.mjt.todoist.tsk.menu.executor;


import bg.sofia.uni.fmi.mjt.todoist.tsk.database.Database;
import bg.sofia.uni.fmi.mjt.todoist.tsk.menu.command.Command;
import bg.sofia.uni.fmi.mjt.todoist.tsk.menu.command.CommandField;
import bg.sofia.uni.fmi.mjt.todoist.tsk.task.GeneralTask;
import bg.sofia.uni.fmi.mjt.todoist.tsk.task.TaskKey;
import bg.sofia.uni.fmi.mjt.todoist.tsk.user.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CommandExecutorTest {
    @Mock
    private Database database;

    @Mock
    private Account account;

    @InjectMocks
    private CommandExecutor executor;

    @Test
    void testExecuteLogout() {
        Command command = new Command("logout", new ArrayList<>());

        assertEquals("Logging out successful!", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteUnknownCommand() {
        Command command = new Command("notKnown", new ArrayList<>());

        assertEquals("Unknown executor command!", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteAddTaskNoName() {
        Command command = new Command("add-task", new ArrayList<>());

        assertEquals("No task name was specified!", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteAddTaskIncorrectDateFormat() {
        List<CommandField> commandFields = List.of(new CommandField("name", "task1"), new CommandField("date", "bad"));

        Command command = new Command("add-task", commandFields);

        assertEquals("Command failed! Incorrect date format! Example format: 20/10/2002", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteAddTaskSuccessful() {
        List<CommandField> commandFields =
            List.of(new CommandField("name", "task1"), new CommandField("date", "10/12/2022"),
                new CommandField("description", "go to war"));

        Command command = new Command("add-task", commandFields);

        GeneralTask generalTask =
            new GeneralTask.TaskBuilder("task1").setDate(LocalDate.of(2022, 12, 10)).setDescription("go to war")
                .build();

        Mockito.when(account.addTask(generalTask)).thenReturn(true);
        Mockito.when(account.getUsername()).thenReturn("user");

        assertEquals("Task added successfully!", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteAddTaskDatabaseProblem() {
        List<CommandField> commandFields =
            List.of(new CommandField("name", "task1"), new CommandField("date", "10/12/2022"),
                new CommandField("description", "go to war"));

        Command command = new Command("add-task", commandFields);

        GeneralTask generalTask =
            new GeneralTask.TaskBuilder("task1").setDate(LocalDate.of(2022, 12, 10)).setDescription("go to war")
                .build();

        Mockito.when(account.addTask(generalTask)).thenReturn(false);

        assertEquals("Task addition failed! Check if the task with the specified name (and date) already exists!",
            executor.execute(command), "Expected command executor output to be correct");
    }

    @Test
    void testExecuteUpdateTaskIncorrectDateFormat() {
        List<CommandField> commandFields = List.of(new CommandField("name", "task1"), new CommandField("date", "bad"));

        Command command = new Command("update-task", commandFields);

        assertEquals("Command failed! Incorrect date format! Example format: 20/10/2002", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteUpdateTaskSuccessfulWithDescription() {
        List<CommandField> commandFields =
            List.of(new CommandField("name", "task1"), new CommandField("date", "10/12/2022"),
                new CommandField("description", "go to war"));

        Command command = new Command("update-task", commandFields);

        GeneralTask generalTask =
            new GeneralTask.TaskBuilder("task1").setDate(LocalDate.of(2022, 12, 10)).setDescription("go to war")
                .build();

        Mockito.when(account.getTask(TaskKey.of(generalTask))).thenReturn(generalTask);
        Mockito.when(account.getUsername()).thenReturn("user");

        assertEquals("Task was updated successfully!", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteUpdateTaskSuccessful() {
        List<CommandField> commandFields = List.of(new CommandField("name", "task1"));

        Command command = new Command("update-task", commandFields);

        GeneralTask generalTask = new GeneralTask.TaskBuilder("task1").build();

        Mockito.when(account.getTask(TaskKey.of(generalTask))).thenReturn(generalTask);
        Mockito.when(account.getUsername()).thenReturn("user");

        assertEquals("Task was updated successfully!", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteUpdateTaskSuccessfulWithDueDate() {
        List<CommandField> commandFields =
            List.of(new CommandField("name", "task1"), new CommandField("date", "10/12/2022"),
                new CommandField("due date", "11/12/2022"));

        Command command = new Command("update-task", commandFields);

        GeneralTask generalTask = new GeneralTask.TaskBuilder("task1").setDate(LocalDate.of(2022, 12, 10))
            .setDueDate(LocalDate.of(2022, 12, 11)).build();

        Mockito.when(account.getTask(TaskKey.of(generalTask))).thenReturn(generalTask);
        Mockito.when(account.getUsername()).thenReturn("user");

        assertEquals("Task was updated successfully!", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteDeleteTaskNoName() {
        Command command = new Command("delete-task", new ArrayList<>());

        assertEquals("No task name was specified!", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteDeleteTaskIncorrectDateFormat() {
        List<CommandField> commandFields = List.of(new CommandField("name", "task1"), new CommandField("date", "bad"));

        Command command = new Command("delete-task", commandFields);

        assertEquals("Command failed! Incorrect date format! Example format: 20/10/2002", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteDeleteTaskSuccessful() {
        List<CommandField> commandFields =
            List.of(new CommandField("name", "task1"), new CommandField("date", "10/12/2022"),
                new CommandField("due date", "11/12/2022"));

        Command command = new Command("delete-task", commandFields);

        GeneralTask generalTask = new GeneralTask.TaskBuilder("task1").setDate(LocalDate.of(2022, 12, 10))
            .setDueDate(LocalDate.of(2022, 12, 11)).build();

        Mockito.when(account.deleteTask(TaskKey.of(generalTask))).thenReturn(true);
        Mockito.when(account.getUsername()).thenReturn("user");

        assertEquals("Task deleted successfully!", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteDeleteTaskDatabaseError() {
        List<CommandField> commandFields =
            List.of(new CommandField("name", "task1"), new CommandField("date", "10/12/2022"),
                new CommandField("due date", "11/12/2022"));

        Command command = new Command("delete-task", commandFields);

        GeneralTask generalTask = new GeneralTask.TaskBuilder("task1").setDate(LocalDate.of(2022, 12, 10))
            .setDueDate(LocalDate.of(2022, 12, 11)).build();

        Mockito.when(account.deleteTask(TaskKey.of(generalTask))).thenReturn(false);

        assertEquals("Task deletion failed! Check if task with specified name (and date) exists!",
            executor.execute(command), "Expected command executor output to be correct\"");
    }

    @Test
    void testExecuteListDashboardEmpty() {
        List<CommandField> commandFields = List.of();

        Command command = new Command("list-dashboard", commandFields);

        Mockito.when(account.getTasksForToday()).thenReturn(List.of());

        assertEquals("There are no tasks for today!", executor.execute(command),
            "Expected command executor output to be correct\"");
    }

    @Test
    void testExecuteListDashboardOneResult() {
        List<CommandField> commandFields = List.of();

        Command command = new Command("list-dashboard", commandFields);

        List<GeneralTask> list = List.of(new GeneralTask.TaskBuilder("name1").build());

        Mockito.when(account.getTasksForToday()).thenReturn(list);

        String output =
            "Printing tasks for today: " + System.lineSeparator() + "{\"name\":\"name1\",\"isFinished\":false}";

        assertEquals(output, executor.execute(command), "Expected command executor output to be correct\"");
    }

    @Test
    void testExecuteListDashboardTwoResults() {
        List<CommandField> commandFields = List.of();

        Command command = new Command("list-dashboard", commandFields);

        List<GeneralTask> list = List.of(new GeneralTask.TaskBuilder("name1").build(),
            new GeneralTask.TaskBuilder("name2").setDescription("i love penguins").build());

        Mockito.when(account.getTasksForToday()).thenReturn(list);

        String output =
            "Printing tasks for today: " + System.lineSeparator() + "{\"name\":\"name1\",\"isFinished\":false}" +
                System.lineSeparator() +
                "{\"name\":\"name2\",\"description\":\"i love penguins\",\"isFinished\":false}";

        assertEquals(output, executor.execute(command), "Expected command executor output to be correct\"");
    }

    @Test
    void testExecuteFinishTaskDatabaseError() {
        List<CommandField> commandFields =
            List.of(new CommandField("name", "task1"), new CommandField("date", "10/12/2022"),
                new CommandField("due date", "11/12/2022"));

        Command command = new Command("finish-task", commandFields);

        GeneralTask generalTask = new GeneralTask.TaskBuilder("task1").setDate(LocalDate.of(2022, 12, 10))
            .setDueDate(LocalDate.of(2022, 12, 11)).build();

        Mockito.when(account.setTaskAsFinished(TaskKey.of(generalTask))).thenReturn(false);

        assertEquals("Could not find task with specified name (and date)!", executor.execute(command),
            "Expected command executor output to be correct\"");
    }

    @Test
    void testExecuteFinishTaskNoName() {
        Command command = new Command("finish-task", new ArrayList<>());

        assertEquals("No task name was specified!", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteFinishTaskIncorrectDateFormat() {
        List<CommandField> commandFields = List.of(new CommandField("name", "task1"), new CommandField("date", "bad"));

        Command command = new Command("finish-task", commandFields);

        assertEquals("Command failed! Incorrect date format! Example format: 20/10/2002", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteFinishTaskSuccessful() {
        List<CommandField> commandFields =
            List.of(new CommandField("name", "task1"), new CommandField("date", "10/12/2022"),
                new CommandField("due date", "11/12/2022"));

        Command command = new Command("finish-task", commandFields);

        GeneralTask generalTask = new GeneralTask.TaskBuilder("task1").setDate(LocalDate.of(2022, 12, 10))
            .setDueDate(LocalDate.of(2022, 12, 11)).build();

        Mockito.when(account.setTaskAsFinished(TaskKey.of(generalTask))).thenReturn(true);

        assertEquals("Task was finished successfully!", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteGetTaskNoName() {
        Command command = new Command("get-task", new ArrayList<>());

        assertEquals("No task name was specified!", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteGetTaskIncorrectDateFormat() {
        List<CommandField> commandFields = List.of(new CommandField("name", "task1"), new CommandField("date", "bad"));

        Command command = new Command("get-task", commandFields);

        assertEquals("Command failed! Incorrect date format! Example format: 20/10/2002", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteGetTaskSuccessful() {
        List<CommandField> commandFields =
            List.of(new CommandField("name", "task1"), new CommandField("date", "10/12/2022"));

        Command command = new Command("get-task", commandFields);

        GeneralTask generalTask = new GeneralTask.TaskBuilder("task1").setDate(LocalDate.of(2022, 12, 10))
            .setDueDate(LocalDate.of(2022, 12, 11)).build();

        Mockito.when(account.getTask(TaskKey.of(generalTask))).thenReturn(generalTask);

        String output = "Task found successfully! Printing info for task: " + System.lineSeparator() +
            "{\"name\":\"task1\",\"date\":\"10-12-2022\",\"dueDate\":\"11-12-2022\",\"isFinished\":false}";

        assertEquals(output, executor.execute(command), "Expected command executor output to be correct");
    }

    @Test
    void testExecuteGetTaskNotFound() {
        List<CommandField> commandFields =
            List.of(new CommandField("name", "task2"), new CommandField("date", "10/12/2022"));

        Command command = new Command("get-task", commandFields);

        GeneralTask generalTask = new GeneralTask.TaskBuilder("task1").setDate(LocalDate.of(2022, 12, 10))
            .setDueDate(LocalDate.of(2022, 12, 11)).build();

        assertEquals("Could not find task with specified name (and date)!", executor.execute(command),
            "Expected command executor output to be correct");
    }


    @Test
    void testExecuteListTasksDateFormat() {
        List<CommandField> commandFields = List.of(new CommandField("name", "task1"), new CommandField("date", "bad"));

        Command command = new Command("list-tasks", commandFields);

        assertEquals("Command failed! Incorrect date format! Example format: 20/10/2002", executor.execute(command),
            "Expected command executor output to be correct");
    }

    @Test
    void testExecuteListTasksNoDateNoCompleted() {
        List<CommandField> commandFields = List.of();

        Command command = new Command("list-tasks", commandFields);

        List<GeneralTask> list = List.of(new GeneralTask.TaskBuilder("name1").build());

        Mockito.when(account.getTasksNoDate()).thenReturn(list);

        String output = "Printing tasks with the specified parameters: " + System.lineSeparator() +
            "{\"name\":\"name1\",\"isFinished\":false}";

        assertEquals(output, executor.execute(command), "Expected command executor output to be correct\"");
    }

    @Test
    void testExecuteListTasksNoDateWithCompleted() {
        List<CommandField> commandFields = List.of(new CommandField("completed", "true"));

        Command command = new Command("list-tasks", commandFields);

        List<GeneralTask> list = List.of(new GeneralTask.TaskBuilder("name1").build());

        Mockito.when(account.getTasksNoDateWithState(true)).thenReturn(list);

        String output = "Printing tasks with the specified parameters: " + System.lineSeparator() +
            "{\"name\":\"name1\",\"isFinished\":false}";

        assertEquals(output, executor.execute(command), "Expected command executor output to be correct\"");
    }

    @Test
    void testExecuteListTasksWithDateWithCompleted() {
        List<CommandField> commandFields =
            List.of(new CommandField("completed", "true"), new CommandField("date", "10/12/2002"));

        Command command = new Command("list-tasks", commandFields);

        List<GeneralTask> list = List.of(new GeneralTask.TaskBuilder("name1").build());

        Mockito.when(account.getTasksForDateWithState(LocalDate.of(2002, 12, 10), true)).thenReturn(list);

        String output = "Printing tasks with the specified parameters: " + System.lineSeparator() +
            "{\"name\":\"name1\",\"isFinished\":false}";

        assertEquals(output, executor.execute(command), "Expected command executor output to be correct\"");
    }

    @Test
    void testExecuteListTasksWithDateNoCompleted() {
        List<CommandField> commandFields = List.of(new CommandField("date", "10/12/2002"));

        Command command = new Command("list-tasks", commandFields);

        List<GeneralTask> list = List.of(new GeneralTask.TaskBuilder("name1").build());

        Mockito.when(account.getTasksForDate(LocalDate.of(2002, 12, 10))).thenReturn(list);

        String output = "Printing tasks with the specified parameters: " + System.lineSeparator() +
            "{\"name\":\"name1\",\"isFinished\":false}";

        assertEquals(output, executor.execute(command), "Expected command executor output to be correct\"");
    }

    @Test
    void testExecuteListTasksEmptyList() {
        Command command = new Command("list-tasks", List.of());

        Mockito.when(account.getTasksNoDate()).thenReturn(List.of());

        assertEquals("There are no tasks with the specified parameters!", executor.execute(command),
            "Expected command executor output to be correct\"");
    }
}
