//package com.example.packs;
//
//import com.example.cards.CardManager;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.Reader;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//
//public class CardManagerReaderWriter {
//
//    private static final Gson GSON = new GsonBuilder()
//            .setPrettyPrinting()
//            .create();
//
//    public static List<CardManager> loadCardManagers() {
//        // Ensure that the JSON file exists
//        ensureCardManagerFileExists();
//
//        String pathToJson = "plugins/roguescape/cardManager.json";
//        try (Reader reader = new FileReader(pathToJson)) {
//
//            return GSON.fromJson(reader, new TypeToken<CardManager>() {
//            }.getType());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static void writeCardManager(List<CardManager> packs)
//    {
//        String filePath = "plugins/roguescape/cardManager.json";
//        Path path = Paths.get(filePath);
//        try
//        {
//            String json = GSON.toJson(packs);
//            Files.write(path, json.getBytes(StandardCharsets.UTF_8));
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    private static void ensureCardManagerFileExists() {
//        try {
//            // 1. Directory path for this user
//            Path dir = Paths.get("plugins/roguescape/");
//            if (!Files.exists(dir)) {
//                Files.createDirectories(dir); // create folders if missing
//            }
//
//            // 2. JSON file path
//            Path file = dir.resolve("cardManager.json");
//            if (!Files.exists(file)) {
//                // 3. Create default JSON content
//                String defaultJson = "[]"; // empty packManager object
//
//                // 4. Write the file
//                Files.write(file, defaultJson.getBytes(StandardCharsets.UTF_8));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
