package bg.sofia.uni.fmi.mjt.todoist.exceptions;

public class CollaborationAlreadyExistsException extends RuntimeException {
    public CollaborationAlreadyExistsException(String message) {
        super(message);
    }
}
