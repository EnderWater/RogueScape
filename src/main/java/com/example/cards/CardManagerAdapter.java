package com.example.cards;

import com.example.JsonManager;
import com.example.tasks.KillTask;
import com.google.gson.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Type;

@Singleton
public class CardManagerAdapter implements JsonSerializer<CardManager>
//        ,JsonDeserializer<CardManager>
{
    private final JsonManager jsonManager;

    @Inject
    public CardManagerAdapter(JsonManager jsonManager) {
        this.jsonManager = jsonManager;
    }

//    @Override
//    public CardManager deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        JsonObject obj = json.getAsJsonObject();
//        int totalPacks = obj.get("totalPacks").getAsInt();
//        int openedPacks = obj.get("openedPacks").getAsInt();
//        int availablePacks = obj.get("availablePacks").getAsInt();
//
//        return new CardManager(this.jsonManager, totalPacks, openedPacks, availablePacks);
//    }

    @Override
    public JsonElement serialize(CardManager cardManager, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("availablePacks", cardManager.getAvailablePacks());
        obj.addProperty("totalPacks", cardManager.getTotalPacks());
        obj.addProperty("openedPacks", cardManager.getOpenedPacks());
        obj.add("heldCards", context.serialize(cardManager.getHeldCards()));

        return obj;
    }
}
