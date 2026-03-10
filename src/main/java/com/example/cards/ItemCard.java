package com.example.cards;

import lombok.Getter;
import net.runelite.api.Item;

public class ItemCard extends Card {
    @Getter
    int itemId;

    ItemCard(String name, String description, String icon, CardRarity rarity, int itemId, int cardId, String type, String packName) {
        super(cardId, name, description, icon, rarity, type, packName);
        this.itemId = itemId;
    }
}
