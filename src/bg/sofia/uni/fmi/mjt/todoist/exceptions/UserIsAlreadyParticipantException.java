package bg.sofia.uni.fmi.mjt.todoist.exceptions;

public class UserIsAlreadyParticipantException extends RuntimeException {
    public UserIsAlreadyParticipantException(String message) {
        super(message);
    }
}
