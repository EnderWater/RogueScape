//package com.example.cards;
//
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
//public class CardReaderWriter {
//
//    private static final Gson GSON = new GsonBuilder()
//            .setPrettyPrinting()
//            .create();
//
//    public static List<Card> loadCards() {
//        // Ensure that the JSON file exists
//        ensureCardFileExists();
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
//    public static void writeCards(List<Card> cards)
//    {
//        String filePath = "plugins/roguescape/cards.json";
//        Path path = Paths.get(filePath);
//        try
//        {
//            String json = GSON.toJson(cards);
//            Files.write(path, json.getBytes(StandardCharsets.UTF_8));
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    private static void ensureCardFileExists() {
//        try {
//            // 1. Directory path for this user
//            Path dir = Paths.get("plugins/roguescape/");
//            if (!Files.exists(dir)) {
//                Files.createDirectories(dir); // create folders if missing
//            }
//
//            // 2. JSON file path
//            Path file = dir.resolve("cards.json");
//            if (!Files.exists(file)) {
//                // 3. Create default JSON content
//                String defaultJson = "[]"; // empty cardManager object
//
//                // 4. Write the file
//                Files.write(file, defaultJson.getBytes(StandardCharsets.UTF_8));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
