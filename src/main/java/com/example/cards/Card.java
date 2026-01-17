package com.example.cards;

public abstract class Card {
    String name;
    String description;
    String icon;

    Card(String name, String description, String icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
}
