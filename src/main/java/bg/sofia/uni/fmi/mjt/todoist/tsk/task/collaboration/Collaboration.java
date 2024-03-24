package bg.sofia.uni.fmi.mjt.todoist.tsk.task.collaboration;

import bg.sofia.uni.fmi.mjt.todoist.tsk.exceptions.TaskDoesNotExistException;
import bg.sofia.uni.fmi.mjt.todoist.tsk.exceptions.UserIsAlreadyParticipantException;
import bg.sofia.uni.fmi.mjt.todoist.tsk.exceptions.UserIsNotParticipantException;
import bg.sofia.uni.fmi.mjt.todoist.tsk.task.CollaborationTask;
import bg.sofia.uni.fmi.mjt.todoist.tsk.task.TaskKey;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Collaboration {

    private final String collaborationName;
    private final String creatorName;
    private final Set<String> participants;
    private final Map<TaskKey, CollaborationTask> collaborationTasks;

    public Collaboration(String collaborationName, String creatorName) {
        this.collaborationName = collaborationName;
        this.creatorName = creatorName;

        participants = new HashSet<>();
        participants.add(creatorName);

        collaborationTasks = new HashMap<>();
    }

    public void addParticipant(String participantName) {
        if (participants.contains(participantName)) {
            throw new UserIsAlreadyParticipantException("User is already added as participant");
        }

        participants.add(participantName);
    }

    public void addAssignee(TaskKey taskKey, String assignee) {
        if (!participants.contains(assignee)) {
            throw new UserIsNotParticipantException("User is not added as a participant");
        }

        if (!collaborationTasks.containsKey(taskKey)) {
            throw new TaskDoesNotExistException("Task with desired parameters does not exist");
        }

        collaborationTasks.get(taskKey).setAssignee(assignee);
    }

    public Collection<CollaborationTask> getTasks() {
        return collaborationTasks.values();
    }

    public String getCollaborationName() {
        return collaborationName;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public Set<String> getParticipants() {
        return participants;
    }

    public Map<TaskKey, CollaborationTask> getCollaborationTasks() {
        return collaborationTasks;
    }

    public boolean addTask(CollaborationTask task) {
        TaskKey taskKey = TaskKey.of(task);
        if (collaborationTasks.containsKey(taskKey)) {
            return false;
        }

        collaborationTasks.put(taskKey, task);
        return true;
    }

    public boolean deleteTask(TaskKey taskKey) {
        if (!collaborationTasks.containsKey(taskKey)) {
            return false;
        }

        collaborationTasks.remove(taskKey);
        return true;
    }

    public boolean setTaskAsFinished(TaskKey taskKey) {
        if (!collaborationTasks.containsKey(taskKey)) {
            return false;
        }

        collaborationTasks.get(taskKey).setFinished(true);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collaboration that = (Collaboration) o;
        return collaborationName.equals(that.collaborationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collaborationName);
    }
}
