package com.example.cards;

import com.google.gson.*;
import java.lang.reflect.Type;

public class CardDeserializer implements JsonDeserializer<Card> {
    @Override
    public Card deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        String cardType = obj.get("type").getAsString();
        int cardId = obj.get("cardId").getAsInt();
        int itemId = obj.get("itemId").getAsInt();
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
                return new ItemCard(name, description, "", cardRarity, itemId, cardId, imageId, cardType);
            case "Boon":
                cardRarity = CardRarity.Mythic;
            default:
                throw new JsonParseException("Unknown cardType: " + cardType);
        }
    }
}
