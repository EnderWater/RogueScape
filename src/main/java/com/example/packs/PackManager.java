package com.example.packs;

import com.example.JsonManager;
import com.google.common.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class PackManager {
    @Getter
    private final Map<String, Pack> packs;
    private final JsonManager jsonManager;

    @Getter
    @Setter
    private String currentPackName;

    @Inject
    public PackManager(JsonManager jsonManager) {
        this.jsonManager = jsonManager;

        Map<String, Pack> packs1;
        packs1 = loadSavedPacks();

        // If there was an issue loading the saved pack data, load the default data instead
        if (packs1 == null || packs1.isEmpty())
            packs1 = loadDefaultPacks();

        packs = packs1;
    }

    private Map<String, Pack> loadDefaultPacks() {
        Path packFile = Paths.get("plugins", "roguescape", "defaultPacks.csv");
        return PackCsvAdapter.readAllCards(packFile);
    }

    private Map<String, Pack> loadSavedPacks() {
        return jsonManager.load("savedPacks.json", new TypeToken<Map<String, Pack>>(){}.getType());
    }

    private void save() {
        jsonManager.save("savedPacks.json", this.packs);
    }

    public void savePacks() {
        save();
    }

    public void addPacks(int num, String packName) {
        Pack currentRegionPack = this.packs.get(packName);

        // Add to the available and opened packs
        currentRegionPack.addAvailablePack(num);
        currentRegionPack.addOpenedPack(num);

        this.save();
    }

    public void addOpenedPacks(String packName) {
        Pack pack = this.packs.get(packName);

        if (pack == null) return;

        pack.addOpenedPack(1);
        pack.addAvailablePack(-1);
    }

    public int getAvailablePacks(String packName) {
        Pack pack = this.packs.get(packName);

        if (pack == null) return -1;

        return pack.getAvailable();
    }

    public Map<Integer, String> getRegionIdToPackNameMap() {
        Map<Integer, String> returnMap = new HashMap<>();

        for (Pack pack : this.packs.values()) {
            for (Integer regionId : pack.getChunkIds()) {
                returnMap.putIfAbsent(regionId, pack.getName());
            }
        }

        return returnMap;
    }

    public List<String> getPackNameList() {
        return new ArrayList<>(packs.keySet());
    }
}
