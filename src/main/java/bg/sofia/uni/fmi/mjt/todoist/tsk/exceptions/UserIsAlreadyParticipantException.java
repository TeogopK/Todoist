package bg.sofia.uni.fmi.mjt.todoist.tsk.exceptions;

public class UserIsAlreadyParticipantException extends RuntimeException {
    public UserIsAlreadyParticipantException(String message) {
        super(message);
    }
}
