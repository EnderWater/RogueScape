package com.example.tasks;

import com.google.gson.*;
import java.lang.reflect.Type;

public class TaskSerializer implements JsonSerializer<Task>
{
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
