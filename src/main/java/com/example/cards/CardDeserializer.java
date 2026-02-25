package com.example.cards;

import com.example.relics.AnthologyOfProficiency;
import com.google.gson.*;
import net.runelite.api.Quest;
import net.runelite.client.eventbus.EventBus;

import java.io.Console;
import java.lang.reflect.Type;

public class CardDeserializer implements JsonDeserializer<Card> {
    private final EventBus eventBus;

    public CardDeserializer(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Card deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        String cardType = obj.get("type").getAsString();
        int cardId = obj.get("cardId").getAsInt();
        int imageId = obj.get("imageId").getAsInt();
        String name = obj.get("name").getAsString();
        String description = obj.get("description").getAsString();

        CardRarity cardRarity;

        try {
            String rarity = obj.get("rarity").getAsString();
            cardRarity = CardRarity.valueOf(rarity);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        switch (cardType) {
            case "Item":
                int itemId = obj.get("itemId").getAsInt();
                return new ItemCard(name, description, "", cardRarity, itemId, cardId, imageId, cardType);
            case "Boon":
                return new BoonCard(cardId, name, description, "", cardRarity, imageId, cardType);
            case "Relic":
                return new RelicCard(cardId, name, description, "", cardRarity, new AnthologyOfProficiency(eventBus), imageId, cardType);
            case "Land":
            case "Quest":
                return new QuestCard(cardId, name, description, "", cardRarity, Quest.DORICS_QUEST, imageId, cardType);
            case "Mini Quest":
            case "Minigame":
                cardRarity = CardRarity.Mythic;
                return new QuestCard(cardId, name, description, "", cardRarity, Quest.DORICS_QUEST, imageId, cardType);

            default:
                throw new JsonParseException("Unknown cardType: " + cardType);
        }
    }
}
