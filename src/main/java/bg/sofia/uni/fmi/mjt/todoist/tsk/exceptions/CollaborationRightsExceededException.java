package bg.sofia.uni.fmi.mjt.todoist.tsk.exceptions;

public class CollaborationRightsExceededException extends RuntimeException {
    public CollaborationRightsExceededException(String message) {
        super(message);
    }
}
