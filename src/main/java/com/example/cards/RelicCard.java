package com.example.cards;

import com.example.relics.Relic;

public class RelicCard extends Card {
    private final Relic relic;

    RelicCard(String name, String description, String icon, CardRarity rarity, Relic relic) {
        super(name, description, icon, rarity);
        this.relic = relic;
    }
}
