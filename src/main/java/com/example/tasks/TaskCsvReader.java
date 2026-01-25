package com.example.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TaskCsvReader {

    public static List<Task> read(Path csvPath) {
        ensureFileExists(csvPath);

        List<Task> tasks = new ArrayList<>();

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

                    String taskType = safe(parts, 0);
                    String name = safe(parts, 1);
                    String description = safe(parts, 2);
                    int current = parseIntSafe(parts, 3);
                    int target = parseIntSafe(parts, 4);
                    boolean pinned = parseBooleanSafe(parts, 5);
                    String monsterType = safe(parts, 6);
                    String skillName = safe(parts, 7);

                    Task task = createTask(
                            taskType,
                            name,
                            description,
                            current,
                            target,
                            pinned,
                            monsterType,
                            skillName
                    );

                    if (task == null) {
                        continue;
                    }

                    tasks.add(task);
                }
                catch (Exception e) {
                    System.err.println(
                            "Skipping bad CSV row at line " + lineNumber + ": " + e.getMessage()
                    );
                }
            }
        }
        catch (IOException e) {
            System.err.println("Failed to read task CSV: " + e.getMessage());
        }

        return tasks;
    }

    private static Task createTask(
            String taskType,
            String name,
            String description,
            int current,
            int target,
            boolean pinned,
            String npcName,
            String skillName
    ) {
        if (taskType.isBlank()) {
            return null;
        }

        switch (taskType.toUpperCase()) {
            case "KILL":
                return new KillTask(
                        "Kill",
                        name,
                        description,
                        current,
                        target,
                        pinned,
                        npcName
                );

            case "SKILL":
                return new SkillTask(
                        "Skill",
                        name,
                        description,
                        current,
                        target,
                        pinned,
                        skillName
                );

            default:
                return null;
        }
    }

    private static void ensureFileExists(Path csvPath) {
        try {
            Files.createDirectories(csvPath.getParent());

            if (!Files.exists(csvPath)) {
                Files.createFile(csvPath);

                // Write CSV header
                Files.writeString(
                        csvPath,
                        "taskType,name,description,current,target,pinned,isComplete,monsterType,skillName\n"
                );
            }
        }
        catch (IOException e) {
            System.err.println("Failed to create task CSV file: " + e.getMessage());
        }
    }


    private static String safe(String[] parts, int index) {
        if (index >= parts.length) {
            return "";
        }
        return parts[index].trim();
    }

    private static int parseIntSafe(String[] parts, int index) {
        try {
            if (index >= parts.length || parts[index].isBlank()) {
                return 0;
            }
            return Integer.parseInt(parts[index].trim());
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    private static boolean parseBooleanSafe(String[] parts, int index) {
        if (index >= parts.length) {
            return false;
        }
        return Boolean.parseBoolean(parts[index].trim());
    }
}
