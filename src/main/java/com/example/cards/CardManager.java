package com.example.cards;

import com.example.JsonManager;
import com.example.tasks.Task;
import com.google.common.reflect.TypeToken;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
public class CardManager {
    @Getter
    private List<Card> allCards = new ArrayList<>();

    // This list contains all the card ids that the user currently has.
    @Getter
    private List<Integer> heldCardIds;

    // This list contains the current 3-5 cards that are visible on the screen while the user is choosing
    @Getter
    private List<OverlayCard> overlayCards = new ArrayList<>();

    @Getter
    private int availablePacks;

    @Getter
    private int totalPacks;

    @Getter
    private int openedPacks;

    @Inject
    public CardManager() {
        CardManager cardManager = JsonManager.load("cardmanager.json", CardManager.class);
        if (cardManager != null) {
            this.totalPacks = cardManager.totalPacks;
            this.openedPacks = cardManager.openedPacks;
            this.availablePacks = cardManager.availablePacks;
        }
        else {
            this.totalPacks = 0;
            this.openedPacks = 0;
            this.availablePacks = 0;
        }
        this.allCards = JsonManager.load("itemCards.json", new TypeToken<List<Card>>(){}.getType());
    }

    public CardManager(int totalPacks, int openedPacks, int availablePacks) {
        this.totalPacks = totalPacks;
        this.openedPacks = openedPacks;
        this.availablePacks = availablePacks;
    }

    private void addAndSavePack() {
        this.availablePacks++;
        JsonManager.save("cardmanager.json", this);
    }

    private void removeAndSavePack() {
        this.availablePacks--;
        JsonManager.save("cardmanager.json", this);
    }

    // This method is used when the total pack needs to increase
    public void addAvailablePack() {
        this.totalPacks++;
        this.addAndSavePack();
    }

    public void openPack() {
        this.addOverlayCards();
    }

    public void closePack() {
        this.overlayCards.clear();
    }

    public void addOverlayCards() {
        Random random = new Random();

        // Push 3 new overlay cards to the array
        for (int i = 0; i < 3; i++) {
            int randInt = random.nextInt(this.allCards.size()-1);
            Card randCard = this.allCards.get(randInt);
            this.overlayCards.add(new OverlayCard(randCard, 100 + (i*100), 100));
        }
    }

    public void completePackOpening() {
        this.overlayCards.clear();
        this.openedPacks++;
        this.removeAndSavePack();
    }
}
