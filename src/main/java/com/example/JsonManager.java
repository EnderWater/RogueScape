package com.example;

import com.example.cards.*;
import com.example.relics.Relic;
import com.example.relics.RelicDeserializer;
import com.example.tasks.Task;
import com.example.tasks.TaskDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonManager {
    private static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .registerTypeAdapter(Relic.class, new RelicDeserializer())
            .registerTypeAdapter(CardManager.class, new CardManagerAdapter())
            .registerTypeAdapter(Card.class, new CardDeserializer())
            .setPrettyPrinting()
            .create();

    public static <T> T load(String filename, Type type) {
        // Ensure that the JSON file exists
        ensureJsonExists(filename);

        String pathToJson = "plugins/roguescape/" + filename;
        try (Reader reader = new FileReader(pathToJson)) {

            return GSON.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void save(String filename, T object)
    {
        String filePath = "plugins/roguescape/" + filename;
        Path path = Paths.get(filePath);
        try
        {
            String json = GSON.toJson(object);
            Files.write(path, json.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void ensureJsonExists(String filename) {
        try {
            // 1. Directory path for this user
            Path dir = Paths.get("plugins/roguescape/");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir); // create folders if missing
            }

            // 2. JSON file path
            Path file = dir.resolve(filename);
            if (!Files.exists(file)) {
                // 3. Create default JSON content
                String defaultJson = ""; // empty cardManager object

                // 4. Write the file
                Files.write(file, defaultJson.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
