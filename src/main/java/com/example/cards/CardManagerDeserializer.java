package com.example.cards;

import com.example.relics.Relic;
import com.example.tasks.KillTask;
import com.google.gson.*;

import java.lang.reflect.Type;

public class CardManagerDeserializer implements JsonDeserializer<CardManager> {
    @Override
    public CardManager deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String taskType = obj.get("relicType").getAsString();

        switch (taskType) {
            case "AnthologyOfProficiency":
                return context.deserialize(json, KillTask.class);
            default:
                throw new JsonParseException("Unknown taskType: " + taskType);
        }
    }
}
