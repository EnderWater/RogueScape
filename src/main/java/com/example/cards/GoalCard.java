package com.example.cards;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the goal packs. When a user selects a GoalCard from a reward pack, they are presented with 3
 * special goal cards that the user can select.
*/
public class GoalCard extends Card {
    private final int packsAwarded;

    GoalCard(int cardId, String name, String description, String icon, CardRarity rarity, String type, String packName, int packsAwarded) {
        super(cardId, name, description, icon, rarity, type, packName);
        this.packsAwarded = packsAwarded;
    }
}
