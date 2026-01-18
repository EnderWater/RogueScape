package com.example.cards;

public abstract class Card {
    String name;
    String description;
    String icon;
    CardRarity rarity;

    Card(String name, String description, String icon, CardRarity rarity) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.rarity = rarity;
    }
}
