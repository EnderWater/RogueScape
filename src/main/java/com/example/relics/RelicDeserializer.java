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
        String taskType = obj.get("relicType").getAsString();

        switch (taskType) {
            case "AnthologyOfProficiency":
                return context.deserialize(json, KillTask.class);
            default:
                throw new JsonParseException("Unknown taskType: " + taskType);
        }
    }
}
