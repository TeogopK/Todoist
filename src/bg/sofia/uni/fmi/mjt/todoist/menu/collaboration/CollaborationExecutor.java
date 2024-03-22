package bg.sofia.uni.fmi.mjt.todoist.menu.collaboration;

import bg.sofia.uni.fmi.mjt.todoist.database.Database;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.CollaborationAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.CollaborationNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.CollaborationRightsExceededException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.TaskDoesNotExistException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.UserDoesNotExistException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.UserIsAlreadyParticipantException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.UserIsNotParticipantException;
import bg.sofia.uni.fmi.mjt.todoist.menu.collaboration.literals.CollaborationExecutorFields;
import bg.sofia.uni.fmi.mjt.todoist.menu.collaboration.literals.CollaborationExecutorResponses;
import bg.sofia.uni.fmi.mjt.todoist.menu.command.Command;
import bg.sofia.uni.fmi.mjt.todoist.menu.executor.CommandExecutor;
import bg.sofia.uni.fmi.mjt.todoist.menu.executor.literals.CommandExecutorResponses;
import bg.sofia.uni.fmi.mjt.todoist.menu.printers.CollaborationPrinter;
import bg.sofia.uni.fmi.mjt.todoist.menu.printers.CollaborationTaskPrinter;
import bg.sofia.uni.fmi.mjt.todoist.task.CollaborationTask;
import bg.sofia.uni.fmi.mjt.todoist.task.TaskKey;
import bg.sofia.uni.fmi.mjt.todoist.user.Account;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CollaborationExecutor extends CommandExecutor {

    private static final String ADD_COLLABORATION = "add-collaboration";
    private static final String DELETE_COLLABORATION = "delete-collaboration";
    private static final String LIST_COLLABORATIONS = "list-collaborations";
    private static final String ADD_USER = "add-user";
    private static final String ASSIGN_TASK = "assign-task";
    private static final String LIST_COLLABORATION_TASKS = "list-collaboration-tasks";
    private static final String LIST_USERS = "list-users";

    private CommandExecutor commandExecutor;

    public CollaborationExecutor(Account userAccount, Database database) {
        super(userAccount, database);

        commandExecutor = new CommandExecutor(userAccount, database);
    }

    @Override
    public String execute(Command cmd) {
        Map<String, String> mapFields = getMapFields(cmd.fields());

        return switch (cmd.commandName()) {
            case ADD_COLLABORATION -> addCollaboration(mapFields);
            case DELETE_COLLABORATION -> deleteCollaboration(mapFields);
            case LIST_COLLABORATIONS -> listCollaborations();
            case ADD_USER -> addUser(mapFields);
            case ASSIGN_TASK -> assignTask(mapFields);
            case LIST_COLLABORATION_TASKS -> listCollaborationTasks(mapFields);
            case LIST_USERS -> listUsers(mapFields);

            default -> commandExecutor.execute(cmd);
        };
    }

    private String listUsers(Map<String, String> mapFields) {
        String collaborationName =
            CollaborationExecutorFields.getField(mapFields, CollaborationExecutorFields.COLLABORATION);

        if (collaborationName == null) {
            return CollaborationExecutorResponses.NO_COLLABORATION_NAME_SPECIFIED.getDescription();
        }

        List<String> results;
        try {
            results = database.listUsersInCollaboration(getUsername(), collaborationName).stream().toList();
        } catch (CollaborationNotFoundException e) {
            return CollaborationExecutorResponses.NO_SUCH_COLLABORATION_FOUND.getDescription();
        }

        return concat(CollaborationExecutorResponses.COLLABORATION_PRINTING_USERS.getDescription(), results);
    }

    private String listCollaborationTasks(Map<String, String> mapFields) {
        String collaborationName =
            CollaborationExecutorFields.getField(mapFields, CollaborationExecutorFields.COLLABORATION);

        if (collaborationName == null) {
            return CollaborationExecutorResponses.NO_COLLABORATION_NAME_SPECIFIED.getDescription();
        }

        Collection<CollaborationTask> tasks;
        try {
            tasks = database.listTasksInCollaboration(getUsername(), collaborationName);
        } catch (CollaborationNotFoundException e) {
            return CollaborationExecutorResponses.NO_SUCH_COLLABORATION_FOUND.getDescription();
        }

        List<String> results = tasks.stream().map(CollaborationTaskPrinter::printCollaborationTask).toList();

        return concat(CollaborationExecutorResponses.COLLABORATION_PRINTING_TASKS.getDescription(), results);
    }

    private String assignTask(Map<String, String> mapFields) {

        String collaborationName =
            CollaborationExecutorFields.getField(mapFields, CollaborationExecutorFields.COLLABORATION);

        if (collaborationName == null) {
            return CollaborationExecutorResponses.NO_COLLABORATION_NAME_SPECIFIED.getDescription();
        }

        String assignee = CollaborationExecutorFields.getField(mapFields, CollaborationExecutorFields.USER);
        if (assignee == null) {
            return CollaborationExecutorResponses.NO_COLLABORATION_USER_SPECIFIED.getDescription();
        }

        TaskKey taskKey = getTaskKey(mapFields);
        if (taskKey == null) {
            return message;
        }

        try {
            database.assignTaskInCollaboration(getUsername(), collaborationName, taskKey, assignee);
        } catch (UserDoesNotExistException e) {
            return CollaborationExecutorResponses.NO_SUCH_USER_FOUND.getDescription();
        } catch (CollaborationNotFoundException e) {
            return CollaborationExecutorResponses.NO_SUCH_COLLABORATION_FOUND.getDescription();
        } catch (UserIsNotParticipantException e) {
            return CollaborationExecutorResponses.USER_NOT_IN_COLLABORATION.getDescription();
        } catch (TaskDoesNotExistException e) {
            return CollaborationExecutorResponses.NO_SUCH_COLLABORATION_TASK.getDescription();
        }

        return CollaborationExecutorResponses.COLLABORATION_ASSIGNEE_ADDED_SUCCESSFULLY.getDescription();
    }


    private String listCollaborations() {
        var collaborations = database.listCollaborations(getUsername());

        List<String> results = collaborations.stream().map(CollaborationPrinter::getNameAndCreator).toList();

        return concat(CollaborationExecutorResponses.COLLABORATION_PRINTING_COLLABORATIONS.getDescription(), results);
    }

    private String deleteCollaboration(Map<String, String> mapFields) {
        String collaborationName =
            CollaborationExecutorFields.getField(mapFields, CollaborationExecutorFields.COLLABORATION);

        if (collaborationName == null) {
            return CollaborationExecutorResponses.NO_COLLABORATION_NAME_SPECIFIED.getDescription();
        }

        try {
            database.deleteCollaboration(getUsername(), collaborationName);
        } catch (CollaborationNotFoundException e) {
            return CollaborationExecutorResponses.NO_SUCH_COLLABORATION_FOUND.getDescription();
        } catch (CollaborationRightsExceededException e) {
            return CollaborationExecutorResponses.COLLABORATION_RIGHTS_EXCEEDED.getDescription();
        }

        return CollaborationExecutorResponses.COLLABORATION_DELETED_SUCCESSFULLY.getDescription();
    }

    private String addUser(Map<String, String> mapFields) {
        String collaborationName =
            CollaborationExecutorFields.getField(mapFields, CollaborationExecutorFields.COLLABORATION);

        if (collaborationName == null) {
            return CollaborationExecutorResponses.NO_COLLABORATION_NAME_SPECIFIED.getDescription();
        }

        String addedUser = CollaborationExecutorFields.getField(mapFields, CollaborationExecutorFields.USER);
        if (addedUser == null) {
            return CollaborationExecutorResponses.NO_COLLABORATION_USER_SPECIFIED.getDescription();
        }

        try {
            database.addUserInCollaboration(getUsername(), collaborationName, addedUser);
        } catch (CollaborationAlreadyExistsException e) {
            return CollaborationExecutorResponses.COLLABORATION_ALREADY_EXISTS_ADDED_USER.getDescription();
        } catch (UserDoesNotExistException e) {
            return CollaborationExecutorResponses.NO_SUCH_USER_FOUND.getDescription();
        } catch (CollaborationNotFoundException e) {
            return CollaborationExecutorResponses.NO_SUCH_COLLABORATION_FOUND.getDescription();
        } catch (UserIsAlreadyParticipantException e) {
            return CollaborationExecutorResponses.USER_ALREADY_IN_COLLABORATION.getDescription();
        }

        return CollaborationExecutorResponses.COLLABORATION_USER_ADDED_SUCCESSFULLY.getDescription();
    }

    private String addCollaboration(Map<String, String> mapFields) {
        String collaborationName =
            CollaborationExecutorFields.getField(mapFields, CollaborationExecutorFields.COLLABORATION);

        if (collaborationName == null) {
            return CollaborationExecutorResponses.NO_COLLABORATION_NAME_SPECIFIED.getDescription();
        }

        try {
            database.addCollaboration(getUsername(), collaborationName);
        } catch (CollaborationAlreadyExistsException e) {
            return CollaborationExecutorResponses.COLLABORATION_ALREADY_EXISTS.getDescription();
        }

        return CollaborationExecutorResponses.COLLABORATION_ADDED_SUCCESSFULLY.getDescription();
    }
}
