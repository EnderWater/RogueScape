package com.example.cards;

import net.runelite.api.Skill;

public class SkillCard extends Card {
    Skill skill;
    SkillCard(int cardId, String name, String description, String icon, CardRarity rarity, Skill skill, int imageId, String type) {
        super(cardId, name, description, icon, rarity, imageId, type);
        this.skill = skill;
    }
}
