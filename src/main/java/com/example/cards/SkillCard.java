package com.example.cards;

import net.runelite.api.Skill;

public class SkillCard extends Card {
    Skill skill;
    SkillCard(String name, String description, String icon, Skill skill) {
        super(name, description, icon);
        this.skill = skill;
    }
}
