package com.example;

import com.google.common.reflect.TypeToken;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class ItemLookup
{
    private final Map<String, Integer> itemByName;

    @Inject
    public ItemLookup(JsonManager jsonManager)
    {
        this.itemByName = jsonManager.load("itemLookupByName.json", new TypeToken<Map<String, Integer>>(){}.getType());
    }

    public Integer getItemId(String name)
    {
        Integer integer = itemByName.get(name);

        if (integer == null) {
            integer = 0;
        }

        return integer;
    }
}