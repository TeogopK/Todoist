package bg.sofia.uni.fmi.mjt.todoist.exceptions;

public class UserIsNotParticipantException extends RuntimeException {
    public UserIsNotParticipantException(String message) {
        super(message);
    }
}
