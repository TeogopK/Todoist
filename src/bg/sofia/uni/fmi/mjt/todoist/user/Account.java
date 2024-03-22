package bg.sofia.uni.fmi.mjt.todoist.user;

import bg.sofia.uni.fmi.mjt.todoist.exceptions.CollaborationAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.CollaborationNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.task.GeneralTask;
import bg.sofia.uni.fmi.mjt.todoist.task.TaskKey;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account {

    private final UserInfo userInfo;

    private final Map<TaskKey, GeneralTask> ownTasks;

    private final Map<String, String> collaborationsIDs; // name, adminUser

    public Account(UserInfo userInfo) {
        this.userInfo = userInfo;
        this.ownTasks = new HashMap<>();
        this.collaborationsIDs = new HashMap<>();
    }

    public Account(UserInfo userInfo, Map<TaskKey, GeneralTask> ownTasks, Map<String, String> collaborationsIDs) {
        this.userInfo = userInfo;
        this.ownTasks = ownTasks;
        this.collaborationsIDs = collaborationsIDs;
    }

    public boolean addTask(GeneralTask generalTask) {
        TaskKey taskKey = TaskKey.of(generalTask);
        if (ownTasks.containsKey(taskKey)) {
            return false;
        }

        ownTasks.put(taskKey, generalTask);
        return true;
    }

    public boolean deleteTask(TaskKey taskKey) {
        if (!ownTasks.containsKey(taskKey)) {
            return false;
        }

        ownTasks.remove(taskKey);
        return true;
    }

    public boolean setTaskAsFinished(TaskKey taskKey) {
        if (!ownTasks.containsKey(taskKey)) {
            return false;
        }

        ownTasks.get(taskKey).setFinished(true);
        return true;
    }

    public GeneralTask getTask(TaskKey taskKey) {
        return ownTasks.get(taskKey);
    }

    public List<GeneralTask> getTasksForToday() {
        LocalDate today = LocalDate.now();
        return getTasksForDate(today);
    }

    public List<GeneralTask> getTasksForDate(LocalDate date) {
        return ownTasks.values().stream().filter(t -> t.getDate() != null).filter(t -> t.getDate().equals(date))
            .toList();
    }

    public List<GeneralTask> getTasksNoDate() {
        return ownTasks.values().stream().filter(t -> t.getDate() == null).toList();
    }

    public List<GeneralTask> getTasksForDateWithState(LocalDate date, boolean onlyFinished) {
        return getTasksForDate(date).stream().filter(t -> t.isFinished() == onlyFinished).toList();
    }

    public List<GeneralTask> getTasksNoDateWithState(boolean onlyFinished) {
        return getTasksNoDate().stream().filter(t -> t.isFinished() == onlyFinished).toList();
    }

    public boolean checkIsCreatorOfCollaboration(String collaborationName) {
        if (!checkHasCollaboration(collaborationName)) {
            throw new CollaborationNotFoundException("User does not participate in the required collaboration");
        }
        return collaborationsIDs.get(collaborationName).equals(userInfo.getUsername());
    }

    public boolean checkHasCollaboration(String collaborationName) {
        return collaborationsIDs.containsKey(collaborationName);
    }

    public String getAdminOfCollaboration(String collaborationName) {
        if (!checkHasCollaboration(collaborationName)) {
            throw new CollaborationNotFoundException("User does not have the required collaboration");
        }
        return collaborationsIDs.get(collaborationName);
    }

    public void addCollaborationAsAdmin(String collaborationName) {
        addCollaboration(collaborationName, userInfo.getUsername());
    }

    public void addCollaboration(String collaborationName, String admin) {
        if (checkHasCollaboration(collaborationName)) {
            throw new CollaborationAlreadyExistsException(
                "User already have the collaboration with the specified name");
        }
        collaborationsIDs.put(collaborationName, admin);
    }

    public void removeCollaboration(String collaborationName) {
        if (!checkHasCollaboration(collaborationName)) {
            throw new CollaborationNotFoundException("User does not have the required collaboration");
        }
        collaborationsIDs.remove(collaborationName);
    }

    public Map<String, String> getCollaborationsIDs() {
        return Collections.unmodifiableMap(collaborationsIDs);
    }

    public Map<TaskKey, GeneralTask> getOwnTasks() {
        return Collections.unmodifiableMap(ownTasks);
    }

    public boolean isPasswordCorrect(String stringPassword) {
        return userInfo.isEnteredPasswordCorrect(stringPassword);
    }

    public String getUsername() {
        return userInfo.getUsername();
    }
}
