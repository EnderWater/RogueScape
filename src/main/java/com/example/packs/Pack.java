package com.example.packs;

import lombok.Getter;

import java.util.List;

public class Pack {
    @Getter
    private final String name;
    private final String regionName;
    @Getter
    private final List<Integer> chunkIds;
    private final String iconName;
    private int available = 0;
    private int opened = 0;
    private int earned = 0;

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
}
