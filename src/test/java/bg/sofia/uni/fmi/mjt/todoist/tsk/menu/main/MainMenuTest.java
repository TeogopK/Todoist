package bg.sofia.uni.fmi.mjt.todoist.tsk.menu.main;

import bg.sofia.uni.fmi.mjt.todoist.tsk.database.Database;
import bg.sofia.uni.fmi.mjt.todoist.tsk.menu.command.Command;
import bg.sofia.uni.fmi.mjt.todoist.tsk.menu.command.CommandField;
import bg.sofia.uni.fmi.mjt.todoist.tsk.user.Account;
import bg.sofia.uni.fmi.mjt.todoist.tsk.user.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MainMenuTest {
    @Mock
    private Database database;

    private final UserInfo userInfo = new UserInfo("name1", "123");

    private final Account account = new Account(userInfo);

    @InjectMocks
    private MainMenu mainMenu;

    @Test
    void testLoadMainMenuQuit() {
        Command command = new Command("quit", new ArrayList<>());

        assertEquals("Quiting...", mainMenu.loadMainMenu(command), "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuLoginEmptyCommandField() {
        Command command = new Command("login", new ArrayList<>());

        assertEquals("Incorrect command format!", mainMenu.loadMainMenu(command),
            "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuLoginNoUsername() {
        List<CommandField> fields = List.of(new CommandField("blaBla", "value"));
        Command command = new Command("login", fields);

        assertEquals("Incorrect command format!", mainMenu.loadMainMenu(command),
            "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuLoginOnlyUsername() {
        List<CommandField> fields = List.of(new CommandField("username", "value"));
        Command command = new Command("login", fields);

        assertEquals("Incorrect command format!", mainMenu.loadMainMenu(command),
            "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuLoginOnlyUsernameAndWrongField() {
        List<CommandField> fields = List.of(new CommandField("username", "value"), new CommandField("wrong", "value"));
        Command command = new Command("login", fields);

        assertEquals("Incorrect command format!", mainMenu.loadMainMenu(command),
            "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuLoginOnlyUsernameTwice() {
        List<CommandField> fields =
            List.of(new CommandField("username", "value"), new CommandField("username", "value"));
        Command command = new Command("login", fields);

        assertEquals("Incorrect command format!", mainMenu.loadMainMenu(command),
            "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuLoginUsernameNotExisting() {
        List<CommandField> fields =
            List.of(new CommandField("username", "value"), new CommandField("password", "value2"));
        Command command = new Command("login", fields);

        assertEquals("Account with such username does not exist!", mainMenu.loadMainMenu(command),
            "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuLoginWrongPassword() {
        List<CommandField> fields = List.of(new CommandField("username", "name1"), new CommandField("password", "123456"));
        Command command = new Command("login", fields);

        Mockito.when(database.doesAccountAlreadyExist("name1")).thenReturn(true);
        Mockito.when(database.getAccount("name1")).thenReturn(account);

        assertEquals("Incorrect password!", mainMenu.loadMainMenu(command), "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuLoginSuccessful() {
        List<CommandField> fields = List.of(new CommandField("username", "name1"), new CommandField("password", "123"));
        Command command = new Command("login", fields);

        Mockito.when(database.doesAccountAlreadyExist("name1")).thenReturn(true);
        Mockito.when(database.getAccount("name1")).thenReturn(account);

        assertEquals("Login successful!", mainMenu.loadMainMenu(command), "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuRegisterUsernameInvalid() {
        List<CommandField> fields =
            List.of(new CommandField("username", "#bad+characters"), new CommandField("password", "value2"));
        Command command = new Command("register", fields);

        assertEquals("Invalid username! Username must contain only latin letters, numbers and underscore!",
            mainMenu.loadMainMenu(command), "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuRegisterOnlyUsername() {
        List<CommandField> fields = List.of(new CommandField("username", "value"));
        Command command = new Command("register", fields);

        assertEquals("Incorrect command format!", mainMenu.loadMainMenu(command),
            "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuRegisterOnlyUsernameAndWrongField() {
        List<CommandField> fields = List.of(new CommandField("username", "value"), new CommandField("wrong", "value"));
        Command command = new Command("register", fields);

        assertEquals("Incorrect command format!", mainMenu.loadMainMenu(command),
            "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuRegisterOnlyUsernameTwice() {
        List<CommandField> fields =
            List.of(new CommandField("username", "value"), new CommandField("username", "value"));
        Command command = new Command("register", fields);

        assertEquals("Incorrect command format!", mainMenu.loadMainMenu(command),
            "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuRegisterUserAlreadyExists() {
        List<CommandField> fields = List.of(new CommandField("username", "name1"), new CommandField("password", "123"));

        Command command = new Command("register", fields);

        Mockito.when(database.doesAccountAlreadyExist("name1")).thenReturn(true);

        assertEquals("Account with such username already exists!", mainMenu.loadMainMenu(command),
            "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuRegisterSuccessful() {
        List<CommandField> fields = List.of(new CommandField("username", "name1"), new CommandField("password", "123"));

        Command command = new Command("register", fields);

        Mockito.when(database.doesAccountAlreadyExist("name1")).thenReturn(false);

        assertEquals("Registration successful! Now please login!", mainMenu.loadMainMenu(command),
            "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuRandomCommand() {
        Command command = new Command("bla-bla", new ArrayList<>());

        assertEquals("Unknown command!", mainMenu.loadMainMenu(command), "Expected main menu output to be correct");
    }

    @Test
    void testLoadMainMenuLogoutSuccessful() {
        List<CommandField> fields = List.of(new CommandField("username", "name1"), new CommandField("password", "123"));
        Command command = new Command("login", fields);

        Mockito.when(database.doesAccountAlreadyExist("name1")).thenReturn(true);
        Mockito.when(database.getAccount("name1")).thenReturn(account);

        assertEquals("Login successful!", mainMenu.loadMainMenu(command), "Expected main menu output to be correct");

        Command commandLogOut = new Command("logout", List.of());

        assertEquals("Unknown command!", mainMenu.loadMainMenu(commandLogOut),
            "Expected main menu output to be correct");

        Command commandThird = new Command("bla-bla", new ArrayList<>());

        assertEquals("Unknown command!", mainMenu.loadMainMenu(commandThird),
            "Expected main menu output to be correct");
    }
}
