package com.example.cards;

import net.runelite.api.Quest;
import net.runelite.api.Skill;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CardCsvLoader {

    public static List<Card> read(Path csvPath) {
        List<Card> cards = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String line;

            // Skip header
            reader.readLine();

            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) continue;

                try {
                    // Split by commas, ignoring commas inside quotes
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                    if (parts.length < 7) continue;

                    String name = safe(parts, 0);
                    String rarityStr = safe(parts, 1);
                    String type = safe(parts, 2);
                    String description = safe(parts, 3);
                    int cardId = parseIntSafe(parts, 4);
                    String packName = safe(parts, 5);
                    String setNumber = safe(parts, 6);
                    int packsAwarded = parseIntSafe(parts, 7);

                    CardRarity rarity;
                    try {
                        rarity = CardRarity.valueOf(rarityStr);
                    } catch (IllegalArgumentException e) {
                        // If there is an issue, just make it a legendary card
                        rarity = CardRarity.Legendary;
                    }

                    Card card = createCard(type, name, description, rarity, cardId, packName, packsAwarded);
                    if (card != null) {
                        cards.add(card);
                    }

                } catch (Exception e) {
                    System.err.println("Skipping bad CSV row at line " + lineNumber + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to read CSV: " + e.getMessage());
        }

        return cards;
    }

    private static Card createCard(String type, String name, String description, CardRarity rarity,
                                   int cardId, String packName, int packsAwarded) {
        switch (type) {
            case "Item":
                // Replace 0 with itemId if you have it in your CSV or a lookup table
                int itemId = 0;
                return new ItemCard(name, description, "", rarity, itemId, cardId, type, packName);

            case "Boon":
                return new BoonCard(cardId, name, description, "", rarity, type, packName);

            case "Goal":
                return new GoalCard(cardId, name, description, "", rarity, type, packName, packsAwarded);

            case "Relic":
//                return new RelicCard(cardId, name, description, "", rarity, new AnthologyOfProficiency(), type, packName);

            case "Land":
            case "Quest":
                return new QuestCard(cardId, name, description, "", rarity, Quest.DORICS_QUEST, type, packName);

            case "Mini Quest":
            case "Minigame":
                return new QuestCard(cardId, name, description, "", CardRarity.Mythic, Quest.DORICS_QUEST, type, packName);

            case "Skill":
                return new SkillCard(cardId, name, description, "", rarity, Skill.AGILITY, type, packName);

            default:
                throw new RuntimeException("Unknown card type: " + type);
        }
    }

    private static String safe(String[] parts, int index) {
        if (index >= parts.length) return "";
        return parts[index].trim().replaceAll("^\"|\"$", ""); // remove quotes
    }

    private static int parseIntSafe(String[] parts, int index) {
        try {
            if (index >= parts.length || parts[index].isBlank()) return 0;
            return Integer.parseInt(parts[index].trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}