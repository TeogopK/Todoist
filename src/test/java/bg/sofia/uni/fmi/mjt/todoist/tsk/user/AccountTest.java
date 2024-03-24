package bg.sofia.uni.fmi.mjt.todoist.tsk.user;

import bg.sofia.uni.fmi.mjt.todoist.tsk.exceptions.CollaborationAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todoist.tsk.exceptions.CollaborationNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.tsk.task.GeneralTask;
import bg.sofia.uni.fmi.mjt.todoist.tsk.task.TaskKey;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountTest {

    private Account account;

    private final GeneralTask generalTask =
        new GeneralTask.TaskBuilder("baby").setDueDate(LocalDate.of(1999, 1, 1)).setDate(LocalDate.of(1999, 1, 2))
            .setDescription("no more").build();

    private final GeneralTask generalTaskNoDate =
        new GeneralTask.TaskBuilder("baby").setDueDate(LocalDate.of(1999, 1, 1)).setDescription("no more").build();

    @Test
    void testAddTask() {
        account = new Account(new UserInfo("vladislav", "dr1234"));

        assertTrue(account.addTask(generalTask), "Expected task to be added");

        assertEquals(1, account.getOwnTasks().size(), "Expected size of tasks to be correct");
        assertEquals(generalTask, account.getOwnTasks().get(TaskKey.of(generalTask)), "Expected tasks to be equal");
    }

    @Test
    void testAddTaskTwice() {
        account = new Account(new UserInfo("vladislav", "dr1234"));

        assertTrue(account.addTask(generalTask), "Expected task to be added");
        assertFalse(account.addTask(generalTask), "Expected task not to be added second time");

        assertEquals(1, account.getOwnTasks().size(), "Expected size of tasks to be correct");
        assertEquals(generalTask, account.getOwnTasks().get(TaskKey.of(generalTask)), "Expected tasks to be equal");
    }

    @Test
    void testAddTwoTasksDifferentByDate() {
        account = new Account(new UserInfo("vladislav", "dr1234"));

        assertTrue(account.addTask(generalTask), "Expected task to be added");
        assertTrue(account.addTask(generalTaskNoDate), "Expected task to be added second time");

        assertEquals(2, account.getOwnTasks().size(), "Expected size of tasks to be correct");
        assertEquals(generalTask, account.getOwnTasks().get(TaskKey.of(generalTask)), "Expected tasks to be equal");
    }

    @Test
    void testAddAndDeleteTask() {
        account = new Account(new UserInfo("vladislav", "dr1234"));
        assertTrue(account.addTask(generalTask), "Expected task to be added");
        assertTrue(account.deleteTask(TaskKey.of(generalTask)), "Expected task to be deleted");

        assertEquals(0, account.getOwnTasks().size(), "Expected size of tasks to be correct");
    }

    @Test
    void testDeleteTask() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();
        map.put(TaskKey.of(generalTask), generalTask);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertTrue(account.deleteTask(TaskKey.of(generalTask)), "Expected task to be deleted");

        assertEquals(0, account.getOwnTasks().size(), "Expected size of tasks to be correct");
    }

    @Test
    void testDeleteTaskTwice() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();
        map.put(TaskKey.of(generalTask), generalTask);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertTrue(account.deleteTask(TaskKey.of(generalTask)), "Expected task to be deleted");
        assertFalse(account.deleteTask(TaskKey.of(generalTask)), "Expected task not to be deleted second time");

        assertEquals(0, account.getOwnTasks().size(), "Expected size of tasks to be correct");
    }

    @Test
    void testDeleteTwoTasksDifferentByDate() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();
        map.put(TaskKey.of(generalTask), generalTask);
        map.put(TaskKey.of(generalTaskNoDate), generalTaskNoDate);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertTrue(account.deleteTask(TaskKey.of(generalTaskNoDate)), "Expected task to be deleted");

        assertEquals(1, account.getOwnTasks().size(), "Expected size of tasks to be correct");
        assertEquals(generalTask, account.getOwnTasks().get(TaskKey.of(generalTask)), "Expected tasks to be equal");
    }

    @Test
    void testGetTask() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();
        map.put(TaskKey.of(generalTask), generalTask);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertEquals(generalTask, account.getTask(TaskKey.of(generalTask)), "Expected task to be identified correctly");

        assertNull(account.getTask(TaskKey.of(generalTaskNoDate)), "Expected null when task is not identified");
    }

    @Test
    void testSetTaskAsFinishedTaskNotFound() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();
        map.put(TaskKey.of(generalTask), generalTask);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertFalse(account.setTaskAsFinished(TaskKey.of(generalTaskNoDate)), "Expected task not to be found");
    }

    @Test
    void testSetTaskAsFinished() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();

        GeneralTask task = new GeneralTask.TaskBuilder("task1").build();
        map.put(TaskKey.of(task), task);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertFalse(account.getOwnTasks().get(TaskKey.of(task)).isFinished(),
            "Expected task to be set false before set");
        assertTrue(account.setTaskAsFinished(TaskKey.of(task)), "Expected task to be set successfully");
        assertTrue(account.getOwnTasks().get(TaskKey.of(task)).isFinished(), "Expected task to be set correctly");
    }

    @Test
    void testGetTasksForToday() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();

        LocalDate today = LocalDate.now();

        GeneralTask task1 = new GeneralTask.TaskBuilder("task1").build();
        GeneralTask task2 = new GeneralTask.TaskBuilder("task2").setDate(today).build();
        GeneralTask task3 = new GeneralTask.TaskBuilder("task3").setDueDate(today).build();

        map.put(TaskKey.of(task1), task1);
        map.put(TaskKey.of(task2), task2);
        map.put(TaskKey.of(task3), task3);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertEquals(List.of(task2), account.getTasksForToday(), "Expected tasks for today to be filtered correctly");
    }

    @Test
    void testGetTasksDate() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();

        LocalDate date = LocalDate.of(1000, 10, 1);

        GeneralTask task1 = new GeneralTask.TaskBuilder("task1").build();
        GeneralTask task2 = new GeneralTask.TaskBuilder("task2").setDate(date).build();
        GeneralTask task3 = new GeneralTask.TaskBuilder("task3").setDueDate(date).build();

        map.put(TaskKey.of(task1), task1);
        map.put(TaskKey.of(task2), task2);
        map.put(TaskKey.of(task3), task3);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertEquals(List.of(task2), account.getTasksForDate(date), "Expected tasks for date to be filtered correctly");
    }

    @Test
    void testGetTasksNoDate() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();

        LocalDate date = LocalDate.of(1000, 10, 1);

        GeneralTask task1 = new GeneralTask.TaskBuilder("task1").build();
        GeneralTask task2 = new GeneralTask.TaskBuilder("task2").setDate(date).build();
        GeneralTask task3 = new GeneralTask.TaskBuilder("task3").setDueDate(date).build();

        map.put(TaskKey.of(task1), task1);
        map.put(TaskKey.of(task2), task2);
        map.put(TaskKey.of(task3), task3);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertEquals(List.of(task1, task3), account.getTasksNoDate(),
            "Expected tasks with no date to be filtered correctly");
    }

    @Test
    void testGetTasksNoDateWithStateTrue() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();

        LocalDate date = LocalDate.of(1000, 10, 1);

        GeneralTask task1 = new GeneralTask.TaskBuilder("task1").build();
        task1.setFinished(true);
        GeneralTask task2 = new GeneralTask.TaskBuilder("task2").setDate(date).build();
        GeneralTask task3 = new GeneralTask.TaskBuilder("task3").setDueDate(date).build();

        map.put(TaskKey.of(task1), task1);
        map.put(TaskKey.of(task2), task2);
        map.put(TaskKey.of(task3), task3);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertEquals(List.of(task1), account.getTasksNoDateWithState(true),
            "Expected tasks with no date and state to be filtered correctly");
    }

    @Test
    void testGetTasksNoDateWithStateFalse() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();

        LocalDate date = LocalDate.of(1000, 10, 1);

        GeneralTask task1 = new GeneralTask.TaskBuilder("task1").build();
        task1.setFinished(true);
        GeneralTask task2 = new GeneralTask.TaskBuilder("task2").setDate(date).build();
        GeneralTask task3 = new GeneralTask.TaskBuilder("task3").setDueDate(date).build();

        map.put(TaskKey.of(task1), task1);
        map.put(TaskKey.of(task2), task2);
        map.put(TaskKey.of(task3), task3);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertEquals(List.of(task3), account.getTasksNoDateWithState(false),
            "Expected tasks with no date and state to be filtered correctly");
    }

    @Test
    void testGetTasksForDateWithStateTrue() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();

        LocalDate date = LocalDate.of(1000, 10, 1);

        GeneralTask task1 = new GeneralTask.TaskBuilder("task1").build();
        task1.setFinished(true);
        GeneralTask task2 = new GeneralTask.TaskBuilder("task2").setDate(date).build();
        GeneralTask task3 = new GeneralTask.TaskBuilder("task3").setDueDate(date).build();
        GeneralTask task4 = new GeneralTask.TaskBuilder("task2").setDate(date).build();
        task4.setFinished(true);


        map.put(TaskKey.of(task1), task1);
        map.put(TaskKey.of(task2), task2);
        map.put(TaskKey.of(task3), task3);
        map.put(TaskKey.of(task4), task4);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertEquals(List.of(task4), account.getTasksForDateWithState(date, true),
            "Expected tasks with date and state to be filtered correctly");
    }

    @Test
    void testGetTasksForDateWithStateFalse() {
        Map<TaskKey, GeneralTask> map = new HashMap<>();

        LocalDate date = LocalDate.of(1000, 10, 1);

        GeneralTask task1 = new GeneralTask.TaskBuilder("task1").build();
        task1.setFinished(true);
        GeneralTask task2 = new GeneralTask.TaskBuilder("task2").setDate(date).build();
        GeneralTask task3 = new GeneralTask.TaskBuilder("task3").setDueDate(date).build();
        GeneralTask task4 = new GeneralTask.TaskBuilder("task2").setDate(date).build();
        task4.setFinished(true);


        map.put(TaskKey.of(task1), task1);
        map.put(TaskKey.of(task2), task2);
        map.put(TaskKey.of(task3), task3);
        map.put(TaskKey.of(task4), task4);

        account = new Account(new UserInfo("vladislav", "dr1234"), map, Map.of());

        assertEquals(List.of(task2), account.getTasksForDateWithState(date, true),
            "Expected tasks with date and state to be filtered correctly");
    }

    @Test
    void testIsPasswordCorrect() {
        account = new Account(new UserInfo("vladislav", "dr1234"), Map.of(), Map.of());

        assertTrue(account.isPasswordCorrect("dr1234"), "Expected password to be correct");
        assertFalse(account.isPasswordCorrect("wrong password"), "Expected password to be wrong");
    }

    @Test
    void testCheckHasCollaboration() {
        Map<String, String> map = new HashMap<>();
        map.put("song", "vlad");

        account = new Account(new UserInfo("vladislav", "dr1234"), Map.of(), map);

        assertTrue(account.checkHasCollaboration("song"), "Expected collaboration to be correctly identified");
        assertFalse(account.checkHasCollaboration("wrong song"), "Expected collaboration not to be identified");
    }

    @Test
    void testCheckIsCreatorOfCollaboration() {
        Map<String, String> map = new HashMap<>();
        map.put("song", "vladislav");
        map.put("Romania", "vlad");

        account = new Account(new UserInfo("vladislav", "dr1234"), Map.of(), map);

        assertTrue(account.checkIsCreatorOfCollaboration("song"),
            "Expected collaboration creator to be correctly identified");

        assertFalse(account.checkIsCreatorOfCollaboration("Romania"),
            "Expected collaboration creator to be correctly identified");
    }

    @Test
    void testCheckIsCreatorOfCollaborationNotFound() {
        Map<String, String> map = new HashMap<>();

        account = new Account(new UserInfo("vladislav", "dr1234"), Map.of(), map);

        assertThrows(CollaborationNotFoundException.class, () -> account.checkIsCreatorOfCollaboration("song"),
            "Expected exception to be thrown when collaboration is not found");
    }

    @Test
    void testGetAdminOfCollaborationNotFound() {
        Map<String, String> map = new HashMap<>();

        account = new Account(new UserInfo("vladislav", "dr1234"), Map.of(), map);

        assertThrows(CollaborationNotFoundException.class, () -> account.getAdminOfCollaboration("song"),
            "Expected exception to be thrown when collaboration is not found");
    }

    @Test
    void testGetAdminOfCollaboration() {
        Map<String, String> map = new HashMap<>();
        map.put("song", "vladislav");
        map.put("Romania", "vlad");

        account = new Account(new UserInfo("vladislav", "dr1234"), Map.of(), map);

        assertEquals("vlad", account.getAdminOfCollaboration("Romania"),
            "Expected collaboration creator to be identified");
    }

    @Test
    void testAddCollaboration() {
        Map<String, String> map = new HashMap<>();
        map.put("song", "vladislav");
        map.put("Romania", "vlad");

        account = new Account(new UserInfo("vladislav", "dr1234"), Map.of(), map);

        account.addCollaboration("world", "Alexander");

        assertEquals(3, account.getCollaborationsIDs().size(), "Expected collaboration to be added correctly");
        assertEquals("Alexander", account.getCollaborationsIDs().get("world"),
            "Expected collaboration to be added correctly");
    }

    @Test
    void testAddCollaborationCollaborationAlreadyExists() {
        Map<String, String> map = new HashMap<>();
        map.put("song", "vladislav");

        account = new Account(new UserInfo("vladislav", "dr1234"), Map.of(), map);

        assertThrows(CollaborationAlreadyExistsException.class, () -> account.addCollaboration("song", "not vlad"),
            "Expected exception to be thrown when collaboration with same name exists");
    }

    @Test
    void testAddCollaborationAsAdminCollaborationAlreadyExists() {
        Map<String, String> map = new HashMap<>();
        map.put("song", "vladislav");

        account = new Account(new UserInfo("vladislav", "dr1234"), Map.of(), map);

        assertThrows(CollaborationAlreadyExistsException.class, () -> account.addCollaborationAsAdmin("song"),
            "Expected exception to be thrown when collaboration with same name exists");
    }

    @Test
    void testAddCollaborationAsAdmin() {
        Map<String, String> map = new HashMap<>();
        map.put("song", "vladislav");
        map.put("Romania", "vlad");

        account = new Account(new UserInfo("vladislav", "dr1234"), Map.of(), map);

        account.addCollaborationAsAdmin("new song");

        assertEquals(3, account.getCollaborationsIDs().size(), "Expected collaboration to be added correctly");
        assertEquals(account.getUsername(), account.getCollaborationsIDs().get("new song"),
            "Expected collaboration to be added correctly");
    }

    @Test
    void testRemoveCollaboration() {
        Map<String, String> map = new HashMap<>();
        map.put("song", "vladislav");
        map.put("Romania", "vlad");

        account = new Account(new UserInfo("vladislav", "dr1234"), Map.of(), map);

        account.removeCollaboration("song");

        assertEquals(1, account.getCollaborationsIDs().size(), "Expected collaboration to be removed correctly");
        assertNull(account.getCollaborationsIDs().get("song"), "Expected collaboration to be removed correctly");
    }

    @Test
    void testRemoveCollaborationNotFound() {
        Map<String, String> map = new HashMap<>();
        map.put("song", "vladislav");

        account = new Account(new UserInfo("vladislav", "dr1234"), Map.of(), map);

        assertThrows(CollaborationNotFoundException.class, () -> account.removeCollaboration("Constantinople"),
            "Expected exception to be thrown when collaboration does not exist");
    }
}
