package com.example.packs;

import com.example.JsonManager;
import com.google.common.reflect.TypeToken;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
@Singleton
public class PackManager {
    @Getter
    private final List<Pack> packs;
    private final JsonManager jsonManager;

    @Inject
    public PackManager(JsonManager jsonManager) {
        this.jsonManager = jsonManager;

        List<Pack> packs1;
        packs1 = loadSavedPacks();

        // If there was an issue loading the saved pack data, load the default data instead
        if (packs1 == null || packs1.isEmpty())
            packs1 = loadDefaultPacks();

        packs = packs1;
    }

    private List<Pack> loadDefaultPacks() {
        Path packFile = Paths.get("plugins", "roguescape", "defaultPacks.csv");
        return PackCsvAdapter.readAllCards(packFile);
    }

    private List<Pack> loadSavedPacks() {
        return jsonManager.load("savedPacks.json", new TypeToken<List<Pack>>(){}.getType());
    }

    private void save() {
        jsonManager.save("savedPacks.json", this.packs);
    }

    public void savePacks() {
        save();
    }
}
