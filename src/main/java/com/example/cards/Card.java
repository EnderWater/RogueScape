package com.example.cards;

import lombok.Getter;

public abstract class Card {
    @Getter
    int cardId;
    @Getter
    String type;
    @Getter
    String name;
    @Getter
    String description;
    @Getter
    String icon;
    @Getter
    CardRarity rarity;
    @Getter
    int imageId;

//    Card(String name, String description, String icon, CardRarity rarity) {
//        this.name = name;
//        this.description = description;
//        this.icon = icon;
//        this.rarity = rarity;
//    }

    Card(int cardId, String name, String description, String icon, CardRarity rarity, int imageId, String type) {
        this.cardId = cardId;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.rarity = rarity;
        this.imageId = imageId;
        this.type = type;
    }
}
