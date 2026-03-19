package com.example.packs;

import com.example.SoundPlayer;
import com.example.overlays.OverlayItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Pack implements OverlayItem {
    @Getter
    private final String name;

    @Getter
    private final List<Integer> chunkIds;

    @Getter
    private int available = 0;

    @Getter
    private int opened = 0;

    @Getter
    private int earned = 0;

    private final String iconName;
    private final String regionName;

    public Pack(String name, String regionName, List<Integer> chunkIds, String iconName) {
        this.name = name;
        this.regionName = regionName;
        this.chunkIds = chunkIds;
        this.iconName = iconName;
    }

    public Pack(String name, String regionName, List<Integer> chunkIds, String iconName, int available, int opened, int earned) {
        this.name = name;
        this.regionName = regionName;
        this.chunkIds = chunkIds;
        this.iconName = iconName;
        this.available = available;
        this.opened = opened;
        this.earned = earned;
    }

    public void addAvailablePack(int num) {
        // Make num positive if it is negative
        if (num < 0)
            num = -num;

        SoundPlayer.play("sounds/Booster_Pack_Obtained_Sound.wav");
        this.available += num;
    }

    public void removeAvailablePack(int num) {
        // Make num positive if it is negative
        if (num < 0)
            num = -num;

        this.available -= num;
    }

    public void addEarnedPack(int num) {
        this.earned += num;
    }

    public void addOpenedPack(int num) {
        this.opened += num;
    }

    @Override
    public String getSearchableString() {
        return getName();
    }
}
