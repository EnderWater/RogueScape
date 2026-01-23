package com.example.cards;

import com.example.tasks.KillTask;
import com.google.gson.*;

import java.lang.reflect.Type;

public class CardManagerAdapter implements JsonSerializer<CardManager>, JsonDeserializer<CardManager> {

    @Override
    public CardManager deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        int totalPacks = obj.get("totalPacks").getAsInt();
        int openedPacks = obj.get("openedPacks").getAsInt();
        int availablePacks = obj.get("availablePacks").getAsInt();

        return new CardManager(totalPacks, openedPacks, availablePacks);
    }

    @Override
    public JsonElement serialize(CardManager cardManager, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("availablePacks", cardManager.getAvailablePacks());
        obj.addProperty("totalPacks", cardManager.getTotalPacks());
        obj.addProperty("openedPacks", cardManager.getOpenedPacks());

        obj.add("heldCardIds", context.serialize(cardManager.getHeldCardIds()));

        return obj;
    }
}
