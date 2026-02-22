package com.example.cards;

import com.example.relics.Relic;

public class RelicCard extends Card {
    private final Relic relic;

    RelicCard(int cardId, String name, String description, String icon, CardRarity rarity, Relic relic, int imageId, String type) {
        super(cardId, name, description, icon, rarity, imageId, type);
        this.relic = relic;
    }
}
