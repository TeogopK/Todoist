package bg.sofia.uni.fmi.mjt.todoist.tsk.database.serializers;

import bg.sofia.uni.fmi.mjt.todoist.tsk.database.adapter.LocalDateAdapter;
import bg.sofia.uni.fmi.mjt.todoist.tsk.task.collaboration.Collaboration;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.file.Files.createDirectory;

public class AllCollaborationsSerializer {

    private static final Gson GSON = LocalDateAdapter.GSON;

    private static final String COLLABORATIONS_DIRNAME = "collaborations";

    private static void createCollaborationFolder() {
        Path path = getCollaborationsDir();

        if (Files.exists(path)) {
            return;
        }

        try {
            createDirectory(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createUserCollaborations(String username) {
        Path userTasks = getUserCollaborationsPath(username);

        if (Files.exists(userTasks)) {
            return;
        }

        try {
            Files.createFile(userTasks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createAllCollaborationsFiles(Collection<String> names) {
        createCollaborationFolder();

        names.forEach(AllCollaborationsSerializer::createUserCollaborations);
    }

    private static Path getCollaborationsDir() {
        return Path.of(UserPasswordsSerializer.ROOT, COLLABORATIONS_DIRNAME);
    }

    private static Path getUserCollaborationsPath(String username) {
        return Path.of(getCollaborationsDir().toString(), username);
    }

    private static Map<String, Collaboration> loadCollaboration(Path path) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {
            return bufferedReader.lines().map(str -> GSON.fromJson(str, Collaboration.class))
                .collect(Collectors.toMap(Collaboration::getCollaborationName, Function.identity()));
        } catch (IllegalStateException | IOException e) {
            throw new IllegalStateException("A problem occurred while working with files", e);
        }
    }

    public static void saveCollaboration(String username, Collection<Collaboration> collaborations) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(
            new FileWriter(getUserCollaborationsPath(username).toFile()))) {

            collaborations.forEach(c -> {
                try {
                    bufferedWriter.append(LocalDateAdapter.GSON.toJson(c)).append(System.lineSeparator());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IllegalStateException | IOException e) {
            throw new IllegalStateException("A problem occurred while working with files", e);
        }
    }

    public static Map<String, Map<String, Collaboration>> loadAllCollaborations(Collection<String> names) {
        Map<String, Map<String, Collaboration>> allCollaborations = new HashMap<>();

        createAllCollaborationsFiles(names);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(getCollaborationsDir())) {

            stream.forEach(p -> allCollaborations.put(p.getFileName().toString(),
                loadCollaboration(getUserCollaborationsPath(p.getFileName().toString()))));

            return allCollaborations;

        } catch (IOException | DirectoryIteratorException e) {
            throw new IllegalStateException("A problem occurred while working with directories", e);
        }
    }

    public static void saveAllCollaborations(Map<String, Map<String, Collaboration>> map) {
        createAllCollaborationsFiles(map.keySet());

        map.forEach((key, value) -> saveCollaboration(key, value.values()));
    }
}
