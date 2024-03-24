package bg.sofia.uni.fmi.mjt.todoist.tsk.exceptions;

public class TaskDoesNotExistException extends RuntimeException {
    public TaskDoesNotExistException(String message) {
        super(message);
    }
}
