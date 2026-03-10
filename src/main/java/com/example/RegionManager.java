package com.example;

import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class RegionManager {
    @Getter
    @Setter
    private int id = 0;

    @Getter
    @Setter
    private String name = "";

    @Getter
    @Setter
    private Map<Integer, String> regionIdsToPackName = new HashMap<>();

    @Inject
    public RegionManager() {

    }
}
