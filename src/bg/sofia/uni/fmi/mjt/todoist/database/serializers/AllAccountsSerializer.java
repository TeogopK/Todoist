package bg.sofia.uni.fmi.mjt.todoist.database.serializers;

import bg.sofia.uni.fmi.mjt.todoist.task.GeneralTask;
import bg.sofia.uni.fmi.mjt.todoist.task.TaskKey;
import bg.sofia.uni.fmi.mjt.todoist.user.Account;
import bg.sofia.uni.fmi.mjt.todoist.user.UserInfo;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Files.createDirectory;

public class AllAccountsSerializer {

    private static final String USERS_DIRNAME = "users";

    public static final String TASKS_PATHNAME = "tasks.txt";
    public static final String COLLABORATIONS_PATHNAME = "collaborations.txt";

    private static void createUsersDir() {
        Path usersPath = getUsersDir();

        if (Files.exists(usersPath)) {
            return;
        }

        try {
            createDirectory(usersPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createUserFolder(String username) {
        Path usernameAsPath = getUsernamePath(username);

        if (Files.exists(usernameAsPath)) {
            return;
        }

        try {
            createDirectory(usernameAsPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createUserTasks(String username) {
        Path userTasks = getUserTasksPath(username);

        if (Files.exists(userTasks)) {
            return;
        }

        try {
            Files.createFile(userTasks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createUserCollaborations(String username) {
        Path userCollaborations = getUserCollaborationsPath(username);

        if (Files.exists(userCollaborations)) {
            return;
        }

        try {
            Files.createFile(userCollaborations);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setAllForUser(String username) {
        createUserFolder(username);
        createUserTasks(username);
        createUserCollaborations(username);
    }


    public static void createAllUserDirs(Collection<String> names) {
        createUsersDir();

        names.forEach(AllAccountsSerializer::setAllForUser);
    }

    public static Map<String, Account> loadAllUserAccounts(Map<String, UserInfo> userInfoMap) {
        Map<String, Account> accountMap = new HashMap<>();

        AllAccountsSerializer.createAllUserDirs(userInfoMap.keySet());

        Map<String, Map<TaskKey, GeneralTask>> ownTasksMap = loadAllTasks();
        Map<String, Map<String, String>> collaborationsIDsMap = loadAllCollaborationsIDs();

        userInfoMap.keySet().forEach(str -> accountMap.put(str,
            new Account(userInfoMap.get(str), ownTasksMap.get(str), collaborationsIDsMap.get(str))));

        return accountMap;
    }

    public static void saveAllUserAccounts(Map<String, Account> accountMap) {
        AllAccountsSerializer.createAllUserDirs(accountMap.keySet());

        saveAllTasks(accountMap);
        saveAllCollaborationsIDs(accountMap);
    }

    private static Map<String, Map<TaskKey, GeneralTask>> loadAllTasks() {
        Map<String, Map<TaskKey, GeneralTask>> allTasks = new HashMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(getUsersDir())) {

            stream.forEach(p -> allTasks.put(p.getFileName().toString(),
                AccountSerializer.loadOwnTasks(getUserTasksPath(p.getFileName().toString()))));

            return allTasks;

        } catch (IOException | DirectoryIteratorException e) {
            throw new IllegalStateException("A problem occurred while working with directories", e);
        }
    }

    private static void saveAllTasks(Map<String, Account> accountMap) {
        accountMap.forEach(
            (key, value) -> AccountSerializer.saveOwnTasks(getUserTasksPath(key), value.getOwnTasks().values()));
    }

    public static void saveOneUserTasks(String name, Collection<GeneralTask> tasks) {
        AccountSerializer.saveOwnTasks(getUserTasksPath(name), tasks);
    }

    public static void saveOneCollaborationIDs(String name, Map<String, String> ids) {
        AccountSerializer.saveCollaborationIDs(getUserCollaborationsPath(name), ids);
    }

    private static Map<String, Map<String, String>> loadAllCollaborationsIDs() {
        Map<String, Map<String, String>> allCollaborations = new HashMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(getUsersDir())) {

            stream.forEach(p -> allCollaborations.put(p.getFileName().toString(),
                AccountSerializer.loadCollaborationIDs(getUserCollaborationsPath(p.getFileName().toString()))));

            return allCollaborations;

        } catch (IOException | DirectoryIteratorException e) {
            throw new IllegalStateException("A problem occurred while working with directories", e);
        }
    }

    private static void saveAllCollaborationsIDs(Map<String, Account> accountMap) {
        accountMap.forEach((key, value) -> AccountSerializer.saveCollaborationIDs(getUserCollaborationsPath(key),
            value.getCollaborationsIDs()));
    }

    private static Path getUsersDir() {
        return Path.of(UserPasswordsSerializer.ROOT, USERS_DIRNAME);
    }

    private static Path getUsernamePath(String username) {
        return Path.of(getUsersDir().toString(), username);
    }

    public static Path getUserTasksPath(String username) {
        return Path.of(getUsernamePath(username).toString(), TASKS_PATHNAME);
    }

    private static Path getUserCollaborationsPath(String username) {
        return Path.of(getUsernamePath(username).toString(), COLLABORATIONS_PATHNAME);
    }

}
