package com.example.cards;

import com.example.overlays.OverlayItem;
import lombok.Getter;

public abstract class Card implements OverlayItem {
    @Getter
    private int cardId;
    @Getter
    private String type;
    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private String icon;
    @Getter
    private CardRarity rarity;
    @Getter
    private String packName;

    Card(int cardId, String name, String description, String icon, CardRarity rarity, String type, String packName) {
        this.cardId = cardId;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.rarity = rarity;
        this.type = type;
        this.packName = packName;
    }

    @Override
    public String getSearchableString() {
        return getDescription() + " " +
                getName() + " " +
                getType() + " " +
                getPackName();
    }
}
