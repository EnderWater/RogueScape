package com.example.cards;

import net.runelite.api.Skill;

public class SkillCard extends Card {
    Skill skill;
    SkillCard(String name, String description, String icon, CardRarity rarity, Skill skill) {
        super(name, description, icon, rarity);
        this.skill = skill;
    }
}
