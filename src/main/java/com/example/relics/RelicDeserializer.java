package com.example.relics;

import com.example.tasks.KillTask;
import com.example.tasks.SkillTask;
import com.example.tasks.Task;
import com.google.gson.*;

import java.lang.reflect.Type;

public class RelicDeserializer implements JsonDeserializer<Relic> {
    @Override
    public Relic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String relicType = obj.get("relicType").getAsString();

        switch (relicType) {
            case "AnthologyOfProficiency":
                return context.deserialize(json, AnthologyOfProficiency.class);
            default:
                throw new JsonParseException("Unknown relicType: " + relicType);
        }
    }
}
