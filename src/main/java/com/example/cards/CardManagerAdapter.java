package com.example.cards;

import com.example.JsonManager;
import com.example.tasks.KillTask;
import com.google.gson.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Type;

@Singleton
public class CardManagerAdapter implements JsonSerializer<CardManager> {
    private final JsonManager jsonManager;

    @Inject
    public CardManagerAdapter(JsonManager jsonManager) {
        this.jsonManager = jsonManager;
    }

    @Override
    public JsonElement serialize(CardManager cardManager, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject obj = new JsonObject();

        obj.add("heldCards", context.serialize(cardManager.getHeldCards()));
        obj.add("openedPackCards", context.serialize(cardManager.getOpenedPackCards()));

        return obj;
    }
}
