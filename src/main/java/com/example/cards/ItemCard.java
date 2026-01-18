package com.example.cards;

import net.runelite.api.Item;

public class ItemCard extends Card {
    Item item;

    ItemCard(String name, String description, String icon, CardRarity rarity, Item item) {
        super(name, description, icon, rarity);
        this.item = item;
    }
}
