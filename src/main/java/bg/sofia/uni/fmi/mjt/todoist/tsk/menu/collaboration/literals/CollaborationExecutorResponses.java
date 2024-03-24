package bg.sofia.uni.fmi.mjt.todoist.tsk.menu.collaboration.literals;

public enum CollaborationExecutorResponses {

    NO_COLLABORATION_NAME_SPECIFIED("No collaboration name specified!"),

    COLLABORATION_ADDED_SUCCESSFULLY("Collaboration was added successfully!"),

    COLLABORATION_DELETED_SUCCESSFULLY("Collaboration deleted successfully!"),

    COLLABORATION_ASSIGNEE_ADDED_SUCCESSFULLY(
        "User was added as assignee successfully to the desired collaboration task!"),

    COLLABORATION_PRINTING_COLLABORATIONS(
        "Printing all current collaborations' names the user participates in: "),

    COLLABORATION_PRINTING_USERS(
        "Printing all current users in the specified collaboration: "),

    COLLABORATION_PRINTING_TASKS(
        "Printing all tasks in the specified collaboration: "),

    COLLABORATION_USER_ADDED_SUCCESSFULLY("User was added successfully to the desired collaboration!"),

    NO_COLLABORATION_USER_SPECIFIED(
        "User specification is required for this command!"),

    NO_SUCH_COLLABORATION_FOUND(
        "There is no such collaboration in the database!"),

    NO_SUCH_USER_FOUND(
        "Specified user does not exist!"),

    NO_SUCH_COLLABORATION_TASK(
        "Specified collaboration task does not exist in the desired collaboration!"),

    COLLABORATION_RIGHTS_EXCEEDED(
        "User has no permission to delete the collaboration, only the creator can!"),

    USER_ALREADY_IN_COLLABORATION(
        "User is already in the specified collaboration!"),

    USER_NOT_IN_COLLABORATION(
        "User is not in the specified collaboration!"),

    COLLABORATION_ALREADY_EXISTS_ADDED_USER(
        "Collaboration with the desired name already exists in the wanted user's collaborations!"),

    COLLABORATION_ALREADY_EXISTS(
        "Collaboration with the desired name already exists!");
    private final String message;

    CollaborationExecutorResponses(String message) {
        this.message = message;
    }

    public String getDescription() {
        return message;
    }
}
