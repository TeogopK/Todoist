package bg.sofia.uni.fmi.mjt.todoist.tsk.database.serializers;

import bg.sofia.uni.fmi.mjt.todoist.tsk.database.adapter.LocalDateAdapter;
import bg.sofia.uni.fmi.mjt.todoist.tsk.task.GeneralTask;
import bg.sofia.uni.fmi.mjt.todoist.tsk.task.TaskKey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AccountSerializer {

    private static final Gson GSON = new GsonBuilder().create();

    public static void saveOwnTasks(Path path, Collection<GeneralTask> ownTasks) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile(), false))) {
            ownTasks.forEach(t -> {
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

    public static Map<TaskKey, GeneralTask> loadOwnTasks(Path path) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {
            return bufferedReader.lines().map(str -> LocalDateAdapter.GSON.fromJson(str, GeneralTask.class))
                .collect(Collectors.toMap(TaskKey::of, Function.identity()));
        } catch (IllegalStateException | IOException e) {
            throw new IllegalStateException("A problem occurred while working with files", e);
        }
    }

    public static void saveCollaborationIDs(Path path, Map<String, String> collaborationIDs) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile(), false))) {
            collaborationIDs.entrySet().stream().map(CollaborationEntrySerializer::of).forEach(t -> {
                try {
                    bufferedWriter.append(GSON.toJson(t)).append(System.lineSeparator());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IllegalStateException | IOException e) {
            throw new IllegalStateException("A problem occurred while working with files", e);
        }
    }

    public static Map<String, String> loadCollaborationIDs(Path path) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {
            return bufferedReader.lines().map(str -> GSON.fromJson(str, CollaborationEntrySerializer.class)).collect(
                Collectors.toMap(CollaborationEntrySerializer::collaborationName,
                    CollaborationEntrySerializer::adminName));
        } catch (IllegalStateException | IOException e) {
            throw new IllegalStateException("A problem occurred while working with files", e);
        }
    }

}
