package com.example.cards;

import net.runelite.api.Quest;

public class QuestCard extends Card {
    Quest quest;
    QuestCard(int cardId, String name, String description, String icon, CardRarity rarity, Quest quest, String type, String packName) {
        super(cardId, name, description, icon, rarity, type, packName);
        this.quest = quest;
    }
}
