package bg.sofia.uni.fmi.mjt.todoist.exceptions;

public class CollaborationRightsExceededException extends RuntimeException {
    public CollaborationRightsExceededException(String message) {
        super(message);
    }
}
