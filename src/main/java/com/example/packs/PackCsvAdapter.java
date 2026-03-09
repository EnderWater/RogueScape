package com.example.packs;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PackCsvAdapter {

    public static List<Pack> readAllCards(Path csvPath) {
        ensureFileExists(csvPath);

        List<Pack> packs = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String line;

            // Skip header
            reader.readLine();

            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                try {
                    String[] parts = line.split(",", -1);

                    if (parts.length < 4) {
                        continue;
                    }

                    String setName = safe(parts, 0);
                    String regionName = safe(parts, 1);
                    List<Integer> chunkIds = parseChunkIds(parts, 2);
                    String iconName = safe(parts, 3);

                    if (setName.isBlank()) {
                        continue;
                    }

                    Pack pack = new Pack(setName, regionName, chunkIds, iconName);
                    packs.add(pack);
                }
                catch (Exception e) {
                    System.err.println(
                            "Skipping bad CSV row at line " + lineNumber + ": " + e.getMessage()
                    );
                }
            }
        }
        catch (IOException e) {
            System.err.println("Failed to read pack CSV: " + e.getMessage());
        }

        return packs;
    }

    public static List<Pack> read(Path csvPath) {
        ensureFileExists(csvPath);

        List<Pack> packs = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String line;

            // Skip header
            reader.readLine();

            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                try {
                    String[] parts = line.split(",", -1);

                    if (parts.length < 6) {
                        continue;
                    }

                    String setName = safe(parts, 0);
                    String regionName = safe(parts, 1);
                    List<Integer> chunkIds = parseChunkIds(parts, 2);
                    String iconName = safe(parts, 3);
                    int available = Integer.parseInt(parts[4]);
                    int opened = Integer.parseInt(parts[5]);
                    int earned = Integer.parseInt(parts[6]);

                    if (setName.isBlank()) {
                        continue;
                    }

                    Pack pack = new Pack(setName, regionName, chunkIds, iconName, available, opened, earned);
                    packs.add(pack);
                }
                catch (Exception e) {
                    System.err.println(
                            "Skipping bad CSV row at line " + lineNumber + ": " + e.getMessage()
                    );
                }
            }
        }
        catch (IOException e) {
            System.err.println("Failed to read pack CSV: " + e.getMessage());
        }

        return packs;
    }

    private static void ensureFileExists(Path csvPath) {
        try {
            Files.createDirectories(csvPath.getParent());

            if (!Files.exists(csvPath)) {
                Files.createFile(csvPath);

                Files.writeString(
                        csvPath,
                        "Set Name,Region Name,Chunk IDs,Pack_Icon\n"
                );
            }
        }
        catch (IOException e) {
            System.err.println("Failed to create pack CSV file: " + e.getMessage());
        }
    }

    private static String safe(String[] parts, int index) {
        if (index >= parts.length) {
            return "";
        }
        return parts[index].trim();
    }

    private static List<Integer> parseChunkIds(String[] parts, int index) {
        List<Integer> chunkIds = new ArrayList<>();

        if (index >= parts.length || parts[index].isBlank()) {
            return chunkIds;
        }

        String[] ids = parts[index].split(";");

        for (String id : ids) {
            try {
                chunkIds.add(Integer.parseInt(id.trim()));
            }
            catch (NumberFormatException ignored) {
            }
        }

        return chunkIds;
    }
}