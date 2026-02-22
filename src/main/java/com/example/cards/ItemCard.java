package com.example.cards;

import lombok.Getter;
import net.runelite.api.Item;

public class ItemCard extends Card {
    @Getter
    Item item;

    ItemCard(String name, String description, String icon, CardRarity rarity, int itemId, int cardId, int imageId, String type) {
        super(cardId, name, description, icon, rarity, imageId, type);
        this.item = new Item(itemId, 1);
    }
}
