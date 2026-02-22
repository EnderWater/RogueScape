package com.example.cards;

import net.runelite.api.Quest;

public class QuestCard extends Card {
    Quest quest;
    QuestCard(int cardId, String name, String description, String icon, CardRarity rarity, Quest quest, int imageId, String type) {
        super(cardId, name, description, icon, rarity, imageId, type);
        this.quest = quest;
    }
}
