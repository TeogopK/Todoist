package bg.sofia.uni.fmi.mjt.todoist.tsk.exceptions;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
