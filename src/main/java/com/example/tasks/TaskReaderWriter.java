package com.example.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TaskReaderWriter {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .setPrettyPrinting()
            .create();

    public static List<Task> loadTasks() {
        // Ensure that the JSON file exists
        ensureTaskFileExists();

        // Just hard-code it since it's not going to change
        String pathToJson = "plugins/roguescape/tasks.json";
        try (Reader reader = new FileReader(pathToJson)) {

            return GSON.fromJson(reader, new TypeToken<List<Task>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeTasks(List<Task> tasks)
    {
        String filePath = "plugins/roguescape/tasks.json";
        Path path = Paths.get(filePath);
        try
        {
            String json = GSON.toJson(tasks);
            Files.write(path, json.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void ensureTaskFileExists() {
        try {
            // 1. Directory path for this user
            Path dir = Paths.get("plugins/roguescape/");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir); // create folders if missing
            }

            // 2. JSON file path
            Path file = dir.resolve("tasks.json");
            if (!Files.exists(file)) {
                // 3. Create default JSON content
                String defaultJson = "[]"; // empty task list

                // 4. Write the file
                Files.write(file, defaultJson.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
