package com.example.cards;

import net.runelite.api.Quest;

public class QuestCard extends Card {
    Quest quest;
    QuestCard(String name, String description, String icon, Quest quest) {
        super(name, description, icon);
        this.quest = quest;
    }
}
