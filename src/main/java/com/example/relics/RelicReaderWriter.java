package com.example.relics;

import com.example.relics.Relic;
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

public class RelicReaderWriter {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Relic.class, new RelicDeserializer())
            .setPrettyPrinting()
            .create();

    public static List<Relic> loadRelics() {
        // Ensure that the JSON file exists
        ensureRelicFileExists();

        // Just hard-code it since it's not going to change
        String pathToJson = "plugins/roguescape/relics.json";
        try (Reader reader = new FileReader(pathToJson)) {
            return GSON.fromJson(reader, new TypeToken<List<Relic>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeRelics(List<Relic> relics)
    {
        String filePath = "plugins/roguescape/relics.json";
        Path path = Paths.get(filePath);
        try
        {
            String json = GSON.toJson(relics);
            Files.write(path, json.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void ensureRelicFileExists() {
        try {
            // 1. Directory path for this user
            Path dir = Paths.get("plugins/roguescape/");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir); // create folders if missing
            }

            // 2. JSON file path
            Path file = dir.resolve("relics.json");
            if (!Files.exists(file)) {
                // 3. Create default JSON content
                String defaultJson = "[]"; // empty relic list

                // 4. Write the file
                Files.write(file, defaultJson.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
