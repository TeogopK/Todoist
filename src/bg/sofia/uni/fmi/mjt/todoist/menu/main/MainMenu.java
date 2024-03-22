package bg.sofia.uni.fmi.mjt.todoist.menu.main;

import bg.sofia.uni.fmi.mjt.todoist.database.Database;
import bg.sofia.uni.fmi.mjt.todoist.menu.collaboration.CollaborationExecutor;
import bg.sofia.uni.fmi.mjt.todoist.menu.command.Command;
import bg.sofia.uni.fmi.mjt.todoist.menu.executor.CommandExecutor;
import bg.sofia.uni.fmi.mjt.todoist.menu.command.CommandField;
import bg.sofia.uni.fmi.mjt.todoist.menu.executor.literals.CommandExecutorResponses;
import bg.sofia.uni.fmi.mjt.todoist.menu.main.literals.MainMenuResponses;
import bg.sofia.uni.fmi.mjt.todoist.menu.main.literals.MainMenuFieldsPositions;
import bg.sofia.uni.fmi.mjt.todoist.user.Account;
import bg.sofia.uni.fmi.mjt.todoist.user.UserInfo;

import java.util.List;

public class MainMenu {
    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    public static final String QUIT = "quit";

    private Database database;
    private String loggedInUser = null;

    public MainMenu(Database database) {
        this.database = database;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUserNull() {
        loggedInUser = null;
    }

    public String loadMainMenu(Command cmd) {
        return inMainMenu(cmd);
    }

    private String inMainMenu(Command cmd) {
        System.out.println("Starting main menu");
        return switch (cmd.commandName()) {
            case LOGIN -> login(cmd.fields());
            case REGISTER -> register(cmd.fields());
            case QUIT -> quit();

            default -> MainMenuResponses.UNKNOWN_COMMAND.getDescription();
        };
    }

    private String login(List<CommandField> fields) {

        if (!MainMenuFieldsPositions.checkCommandField(fields)) {
            return MainMenuResponses.INCORRECT_COMMAND_FORMAT.getDescription();
        }

        String username = MainMenuFieldsPositions.getField(fields, MainMenuFieldsPositions.USERNAME);

        String password = MainMenuFieldsPositions.getField(fields, MainMenuFieldsPositions.PASSWORD);

        if (!database.doesAccountAlreadyExist(username)) {
            return MainMenuResponses.NOT_EXISTING_USERNAME.getDescription();
        }

        Account account = database.getAccount(username);

        if (!account.isPasswordCorrect(password)) {
            return MainMenuResponses.INCORRECT_PASSWORD.getDescription();
        }

        loggedInUser = username;
        System.out.println("Logged user " + username);

        return MainMenuResponses.LOGIN_SUCCESSFUL.getDescription();
    }

    private String register(List<CommandField> fields) {
        if (!MainMenuFieldsPositions.checkCommandField(fields)) {
            return MainMenuResponses.INCORRECT_COMMAND_FORMAT.getDescription();
        }

        String username = MainMenuFieldsPositions.getField(fields, MainMenuFieldsPositions.USERNAME);

        if (!UserInfo.isUsernameValid(username)) {
            return MainMenuResponses.INVALID_USERNAME.getDescription();
        }

        if (database.doesAccountAlreadyExist(username)) {
            return MainMenuResponses.ALREADY_EXISTING_USERNAME.getDescription();
        }

        String password = MainMenuFieldsPositions.getField(fields, MainMenuFieldsPositions.PASSWORD);

        UserInfo userInfo = new UserInfo(username, password);

        database.addAccount(userInfo);

        return MainMenuResponses.REGISTER_SUCCESSFUL.getDescription();
    }

    private String quit() {
        return MainMenuResponses.QUITING.getDescription();
    }

    public void saveDatabase() {
        database.saveDatabase();
    }
}
