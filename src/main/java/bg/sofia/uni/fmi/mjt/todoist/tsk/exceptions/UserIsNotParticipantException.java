package bg.sofia.uni.fmi.mjt.todoist.tsk.exceptions;

public class UserIsNotParticipantException extends RuntimeException {
    public UserIsNotParticipantException(String message) {
        super(message);
    }
}
