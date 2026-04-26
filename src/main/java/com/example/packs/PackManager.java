package com.example.packs;

import com.example.JsonManager;
import com.example.overlays.OverlayStateManager;
import com.google.common.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class PackManager {
    private final Map<String, Pack> packs;
    private final JsonManager jsonManager;
    private final OverlayStateManager overlayStateManager;
    private final int MAX_PACKS_PER_PAGE = 6;

    @Getter
    @Setter
    private String currentPackName;

    @Getter
    private final List<String> packNames;

    @Getter
    private final List<Pack> sortedPacks = new ArrayList<>();

    @Inject
    public PackManager(JsonManager jsonManager, OverlayStateManager overlayStateManager) {
        this.jsonManager = jsonManager;
        this.overlayStateManager = overlayStateManager;

        Map<String, Pack> packs1;
        packs1 = loadSavedPacks();

        // If there was an issue loading the saved pack data, load the default data instead
        if (packs1 == null || packs1.isEmpty())
            packs1 = loadDefaultPacks();

        packs = packs1;

        //  Load the packNames
        this.packNames = packs.keySet().stream().sorted().collect(Collectors.toList());

        // Get and sort the list of packs based on their available packs
        this.sortedPacks.addAll(packs.values());
        this.sortPacks();
    }

    public Collection<Pack> getPacks() {
        return packs.values();
    }

    private Map<String, Pack> loadDefaultPacks() {
        Path packFile = Paths.get("assets", "data", "defaultPacks.csv");
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

    public void addCurrentRegionPacks(int num) {
        addAvailablePacks(currentPackName, num);
        this.save();
    }

    public void completePackOpening(String packName, int addOpenedCount, int removeAvailableCount) {
        this.addEarnedPacks(packName, addOpenedCount);
        this.removeAvailablePack(packName, removeAvailableCount);
        this.sortPacks();
    }

    public void addAvailablePacks(String packName, int count) {
        Pack pack = this.packs.get(packName);

        if (pack == null) return;

        pack.addAvailablePack(count);
    }

    public void addEarnedPacks(String packName, int count) {
        Pack pack = this.packs.get(packName);

        if (pack == null) return;

        pack.addEarnedPack(count);
    }

    public void removeAvailablePack(String packName, int count) {
        Pack pack = this.packs.get(packName);

        if (pack == null) return;

        pack.removeAvailablePack(count);
    }

    public int getAvailablePacks(String packName) {
        Pack pack = this.packs.get(packName);

        if (pack == null) return -1;

        return pack.getAvailable();
    }

    public List<String> getOverlayPackNames() {
        if (this.packNames.size() < this.packs.size())
            return new ArrayList<>(this.packs.keySet());
        return this.packNames;
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

    private void sortPacks() {
        this.sortedPacks.sort(Comparator.comparing(pack -> -pack.getAvailable()));
    }

    public void openAllPacksOverlay() {
        // Since the packs stored in this manager are in a map, just get the values and store them as a list to give to the overlay
        this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.AllPacks, sortedPacks, MAX_PACKS_PER_PAGE);
    }
}
