package com.example.cards;

import net.runelite.api.Skill;

public class SkillCard extends Card {
    Skill skill;
    SkillCard(int cardId, String name, String description, String icon, CardRarity rarity, Skill skill, String type, String packName) {
        super(cardId, name, description, icon, rarity, type, packName);
        this.skill = skill;
    }
}
