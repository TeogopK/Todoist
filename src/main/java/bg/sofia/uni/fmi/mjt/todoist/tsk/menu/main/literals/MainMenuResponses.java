package bg.sofia.uni.fmi.mjt.todoist.tsk.menu.main.literals;

public enum MainMenuResponses {
    INVALID_USERNAME("Invalid username! Username must contain only latin letters, numbers and underscore!"),
    NOT_EXISTING_USERNAME("Account with such username does not exist!"),
    ALREADY_EXISTING_USERNAME("Account with such username already exists!"),
    INCORRECT_PASSWORD("Incorrect password!"),
    INCORRECT_COMMAND_FORMAT("Incorrect command format!"),
    LOGIN_SUCCESSFUL("Login successful!"),
    REGISTER_SUCCESSFUL("Registration successful! Now please login!"),
    UNKNOWN_COMMAND("Unknown command!"),
    QUITING("Quiting...");

    private final String message;

    MainMenuResponses(String message) {
        this.message = message;
    }

    public String getDescription() {
        return message;
    }
}
