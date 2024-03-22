package bg.sofia.uni.fmi.mjt.todoist.menu.collaboration;

import bg.sofia.uni.fmi.mjt.todoist.database.Database;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.CollaborationAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.CollaborationNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.CollaborationRightsExceededException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.TaskDoesNotExistException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.UserDoesNotExistException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.UserIsAlreadyParticipantException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.UserIsNotParticipantException;
import bg.sofia.uni.fmi.mjt.todoist.menu.command.Command;
import bg.sofia.uni.fmi.mjt.todoist.menu.command.CommandField;
import bg.sofia.uni.fmi.mjt.todoist.task.CollaborationTask;
import bg.sofia.uni.fmi.mjt.todoist.task.TaskKey;
import bg.sofia.uni.fmi.mjt.todoist.task.collaboration.Collaboration;
import bg.sofia.uni.fmi.mjt.todoist.user.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;


@ExtendWith(MockitoExtension.class)
public class CollaborationExecutorTest {
    @Mock
    private Database database;

    @Mock
    private Account account;

    @InjectMocks
    private CollaborationExecutor executor;

    @Test
    void testExecuteAddCollaborationNoCollaborationName() {
        Command command = new Command("add-collaboration", new ArrayList<>());

        assertEquals("No collaboration name specified!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAddCollaborationAlreadyExists() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));
        Command command = new Command("add-collaboration", commandFields);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(CollaborationAlreadyExistsException.class).when(database).addCollaboration("user", "collab1");

        assertEquals("Collaboration with the desired name already exists!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAddCollaborationSuccessful() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));
        Command command = new Command("add-collaboration", commandFields);

        assertEquals("Collaboration was added successfully!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteDeleteCollaborationNoCollaborationName() {
        Command command = new Command("delete-collaboration", new ArrayList<>());

        assertEquals("No collaboration name specified!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteDeleteCollaborationNotExisting() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));
        Command command = new Command("delete-collaboration", commandFields);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(CollaborationNotFoundException.class).when(database).deleteCollaboration("user", "collab1");

        assertEquals("There is no such collaboration in the database!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteDeleteCollaborationRightsExceeding() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));
        Command command = new Command("delete-collaboration", commandFields);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(CollaborationRightsExceededException.class).when(database).deleteCollaboration("user", "collab1");

        assertEquals("User has no permission to delete the collaboration, only the creator can!",
            executor.execute(command), "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteDeleteCollaborationSuccessful() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));
        Command command = new Command("delete-collaboration", commandFields);

        assertEquals("Collaboration deleted successfully!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAddUserCollaborationNoCollaborationName() {
        Command command = new Command("add-user", new ArrayList<>());

        assertEquals("No collaboration name specified!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAddUserUserNotSpecified() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));

        Command command = new Command("add-user", commandFields);

        assertEquals("User specification is required for this command!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAddUserUserDoesNotExist() {
        List<CommandField> commandFields =
            List.of(new CommandField("collaboration", "collab1"), new CommandField("user", "addedUser"));
        Command command = new Command("add-user", commandFields);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(UserDoesNotExistException.class).when(database).addUserInCollaboration("user", "collab1", "addedUser");

        assertEquals("Specified user does not exist!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAddUserCollaborationAlreadyExist() {
        List<CommandField> commandFields =
            List.of(new CommandField("collaboration", "collab1"), new CommandField("user", "addedUser"));
        Command command = new Command("add-user", commandFields);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(CollaborationAlreadyExistsException.class).when(database)
            .addUserInCollaboration("user", "collab1", "addedUser");

        assertEquals("Collaboration with the desired name already exists in the wanted user's collaborations!",
            executor.execute(command), "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAddUserNoSuchCollaboration() {
        List<CommandField> commandFields =
            List.of(new CommandField("collaboration", "collab1"), new CommandField("user", "addedUser"));
        Command command = new Command("add-user", commandFields);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(CollaborationNotFoundException.class).when(database)
            .addUserInCollaboration("user", "collab1", "addedUser");

        assertEquals("There is no such collaboration in the database!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAddUserAlreadyParticipant() {
        List<CommandField> commandFields =
            List.of(new CommandField("collaboration", "collab1"), new CommandField("user", "addedUser"));
        Command command = new Command("add-user", commandFields);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(UserIsAlreadyParticipantException.class).when(database)
            .addUserInCollaboration("user", "collab1", "addedUser");

        assertEquals("User is already in the specified collaboration!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAddUserSuccessful() {
        List<CommandField> commandFields =
            List.of(new CommandField("collaboration", "collab1"), new CommandField("user", "addedUser"));

        Command command = new Command("add-user", commandFields);

        assertEquals("User was added successfully to the desired collaboration!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteListUsersNoCollaborationName() {
        Command command = new Command("list-users", new ArrayList<>());

        assertEquals("No collaboration name specified!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteListUsersNoSuchCollaboration() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));
        Command command = new Command("list-users", commandFields);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(CollaborationNotFoundException.class).when(database).listUsersInCollaboration("user", "collab1");

        assertEquals("There is no such collaboration in the database!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteListUsersOneResult() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));
        Command command = new Command("list-users", commandFields);

        List<String> results = List.of("player1");

        Mockito.when(account.getUsername()).thenReturn("user");
        Mockito.when(database.listUsersInCollaboration("user", "collab1")).thenReturn(results);

        String output =
            "Printing all current users in the specified collaboration: " + System.lineSeparator() + "player1";

        assertEquals(output, executor.execute(command), "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteListUsersTwoResults() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));
        Command command = new Command("list-users", commandFields);

        List<String> results = List.of("player1", "player2");

        Mockito.when(account.getUsername()).thenReturn("user");
        Mockito.when(database.listUsersInCollaboration("user", "collab1")).thenReturn(results);

        String output =
            "Printing all current users in the specified collaboration: " + System.lineSeparator() + "player1" +
                System.lineSeparator() + "player2";

        assertEquals(output, executor.execute(command), "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteListCollaborations() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));
        Command command = new Command("list-collaborations", commandFields);

        Collaboration collaboration1 = new Collaboration("get the frankish empire", "frank");
        Collaboration collaboration2 = new Collaboration("get the byzantine empire", "constantine");

        List<Collaboration> list = List.of(collaboration1, collaboration2);

        Mockito.when(account.getUsername()).thenReturn("user");
        Mockito.when(database.listCollaborations("user")).thenReturn(list);

        String output =
            "Printing all current collaborations' names the user participates in: " + System.lineSeparator() +
                "{\"collaborationName\":\"get the frankish empire\",\"adminName\":\"frank\"}" + System.lineSeparator() +
                "{\"collaborationName\":\"get the byzantine empire\",\"adminName\":\"constantine\"}";

        assertEquals(output, executor.execute(command), "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteListTasksNoCollaborationName() {
        Command command = new Command("list-collaboration-tasks", new ArrayList<>());

        assertEquals("No collaboration name specified!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteListTasksCollaborationNotFound() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));
        Command command = new Command("list-collaboration-tasks", commandFields);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(CollaborationNotFoundException.class).when(database).listTasksInCollaboration("user", "collab1");

        assertEquals("There is no such collaboration in the database!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteListTasksSuccessfulNoTasks() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));
        Command command = new Command("list-collaboration-tasks", commandFields);

        Mockito.when(account.getUsername()).thenReturn("user");
        Mockito.when(database.listTasksInCollaboration("user", "collab1")).thenReturn(List.of());

        assertEquals("Printing all tasks in the specified collaboration: ", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteListTasksSuccessfulTwoTasks() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));
        Command command = new Command("list-collaboration-tasks", commandFields);

        List<CollaborationTask> tasks =
            List.of(new CollaborationTask.CollaborationTaskBuilder("take over the planet").build(),
                new CollaborationTask.CollaborationTaskBuilder("take over the world").setDescription("slowly").build());

        Mockito.when(account.getUsername()).thenReturn("user");
        Mockito.when(database.listTasksInCollaboration("user", "collab1")).thenReturn(tasks);

        String output = "Printing all tasks in the specified collaboration: " + System.lineSeparator() +
            "{\"name\":\"take over the planet\",\"isFinished\":false}" + System.lineSeparator() +
            "{\"name\":\"take over the world\",\"description\":\"slowly\",\"isFinished\":false}";

        assertEquals(output, executor.execute(command), "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAssignTaskNoCollaborationName() {
        Command command = new Command("assign-task", new ArrayList<>());

        assertEquals("No collaboration name specified!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAssignTaskNoSpecifiedUser() {
        List<CommandField> commandFields = List.of(new CommandField("collaboration", "collab1"));

        Command command = new Command("assign-task", commandFields);

        assertEquals("User specification is required for this command!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAssignTaskUserDoesNotExist() {
        List<CommandField> commandFields =
            List.of(new CommandField("collaboration", "collab1"), new CommandField("user", "addedUser"),
                new CommandField("name", "task1"));
        Command command = new Command("assign-task", commandFields);

        TaskKey taskKey = new TaskKey("task1", null);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(UserDoesNotExistException.class).when(database)
            .assignTaskInCollaboration("user", "collab1", taskKey, "addedUser");

        assertEquals("Specified user does not exist!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAssignTaskTaskKeyInvalid() {
        List<CommandField> commandFields =
            List.of(new CommandField("collaboration", "collab1"), new CommandField("user", "addedUser"));
        Command command = new Command("assign-task", commandFields);

        assertEquals("No task name was specified!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAssignTaskCollaborationNotFound() {
        List<CommandField> commandFields =
            List.of(new CommandField("collaboration", "collab1"), new CommandField("user", "addedUser"),
                new CommandField("name", "task1"));
        Command command = new Command("assign-task", commandFields);

        TaskKey taskKey = new TaskKey("task1", null);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(CollaborationNotFoundException.class).when(database)
            .assignTaskInCollaboration("user", "collab1", taskKey, "addedUser");

        assertEquals("There is no such collaboration in the database!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAssignTaskUserNotParticipant() {
        List<CommandField> commandFields =
            List.of(new CommandField("collaboration", "collab1"), new CommandField("user", "addedUser"),
                new CommandField("name", "task1"));
        Command command = new Command("assign-task", commandFields);

        TaskKey taskKey = new TaskKey("task1", null);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(UserIsNotParticipantException.class).when(database)
            .assignTaskInCollaboration("user", "collab1", taskKey, "addedUser");

        assertEquals("User is not in the specified collaboration!", executor.execute(command),
            "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAssignTaskTaskDoesNotExist() {
        List<CommandField> commandFields =
            List.of(new CommandField("collaboration", "collab1"), new CommandField("user", "addedUser"),
                new CommandField("name", "task1"));
        Command command = new Command("assign-task", commandFields);

        TaskKey taskKey = new TaskKey("task1", null);

        Mockito.when(account.getUsername()).thenReturn("user");

        doThrow(TaskDoesNotExistException.class).when(database)
            .assignTaskInCollaboration("user", "collab1", taskKey, "addedUser");

        assertEquals("Specified collaboration task does not exist in the desired collaboration!",
            executor.execute(command), "Expected collaboration executor output to be correct");
    }

    @Test
    void testExecuteAssignTaskSuccessful() {
        List<CommandField> commandFields =
            List.of(new CommandField("collaboration", "collab1"), new CommandField("user", "addedUser"),
                new CommandField("name", "task1"));
        Command command = new Command("assign-task", commandFields);

        Mockito.when(account.getUsername()).thenReturn("user");

        assertEquals("User was added as assignee successfully to the desired collaboration task!",
            executor.execute(command), "Expected collaboration executor output to be correct");
    }

}
