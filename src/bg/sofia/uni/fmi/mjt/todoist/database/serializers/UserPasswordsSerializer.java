package bg.sofia.uni.fmi.mjt.todoist.database.serializers;

import bg.sofia.uni.fmi.mjt.todoist.database.adapter.LocalDateAdapter;
import bg.sofia.uni.fmi.mjt.todoist.user.UserInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserPasswordsSerializer {

    private static final Gson GSON = new Gson();

    public static final String ROOT = "database";
    private static final String USERS_PASSWORDS_FILENAME = "usersList.txt";

    public static final Path ROOT_PATH = Path.of(ROOT);

    private static final Path USERS_PASSWORDS_FILEPATH = concatWithRoot(USERS_PASSWORDS_FILENAME);

    private static void createMainDirectory() {
        if (Files.exists(ROOT_PATH)) {
            return;
        }

        try {
            Files.createDirectory(ROOT_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createUserListsFile() {
        if (Files.exists(USERS_PASSWORDS_FILEPATH)) {
            return;
        }

        try {
            Files.createFile(USERS_PASSWORDS_FILEPATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path concatWithRoot(String childPath) {
        return Path.of(ROOT, childPath);
    }

    private static Map<String, UserInfo> loadUserPasswords(Path path) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {

            return bufferedReader.lines().map(str -> GSON.fromJson(str, UserInfo.class))
                .collect(Collectors.toMap(UserInfo::getUsername, Function.identity()));

        } catch (IllegalStateException | IOException e) {
            throw new IllegalStateException("A problem occurred while working with files", e);
        }
    }

    private static void saveUserPasswords(Path path, Collection<UserInfo> userSet) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile()))) {
            userSet.forEach(t -> {
                try {
                    bufferedWriter.append(LocalDateAdapter.GSON.toJson(t)).append(System.lineSeparator());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IllegalStateException | IOException e) {
            throw new IllegalStateException("A problem occurred while working with files", e);
        }
    }

    public static Map<String, UserInfo> getUserPasswords() {
        createMainDirectory();
        createUserListsFile();

        return loadUserPasswords(USERS_PASSWORDS_FILEPATH);
    }

    public static void saveUserPasswords(Collection<UserInfo> userSet) {
        createMainDirectory();
        createUserListsFile();

        saveUserPasswords(USERS_PASSWORDS_FILEPATH, userSet);
    }
}
