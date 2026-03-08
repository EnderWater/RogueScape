package com.example.tasks;

import com.example.JsonManager;
import com.google.gson.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Type;

@Singleton
public class TaskAdapter implements JsonSerializer<Task>, JsonDeserializer<Task> {
    private final JsonManager jsonManager;

    @Inject
    public TaskAdapter(JsonManager jsonManager) {
        this.jsonManager = jsonManager;
    }

    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String taskType = obj.get("taskType").getAsString();

        switch (taskType) {
            case "Kill":
                return context.deserialize(json, KillTask.class);
            case "Skill":
                return context.deserialize(json, SkillTask.class);
            case "Quest":
                return context.deserialize(json, QuestTask.class);
            case "Miscellaneous":
                return context.deserialize(json, MiscellaneousTask.class);
            default:
                throw new JsonParseException("Unknown taskType: " + taskType);
        }
    }

    @Override
    public JsonElement serialize(Task task, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject obj = (JsonObject) context.serialize(task);

        if (task instanceof KillTask)
        {
            obj.addProperty("taskType", "Kill");
        }
        else if (task instanceof SkillTask)
        {
            obj.addProperty("taskType", "Skill");
        }

        return obj;
    }
}
