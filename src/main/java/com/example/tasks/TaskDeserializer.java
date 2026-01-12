package com.example.tasks;

import com.google.gson.*;
import java.lang.reflect.Type;

public class TaskDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String taskType = obj.get("taskType").getAsString();

        switch (taskType) {
            case "Kill":
                return context.deserialize(json, KillTask.class);
            case "Skill":
                return context.deserialize(json, SkillTask.class);
            default:
                throw new JsonParseException("Unknown taskType: " + taskType);
        }
    }
}