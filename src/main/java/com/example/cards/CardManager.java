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
    private final JsonManager jsonManager;

    @Getter
    private List<Card> allCards = new ArrayList<>();

    // This list contains all the cards that the user currently has.
    @Getter
    private List<Card> heldCards = new ArrayList<>();

    // This list contains the current 3-5 cards that are visible on the screen while the user is choosing
    @Getter
    private List<Card> overlayCards = new ArrayList<>();

    @Getter
    private int availablePacks;

    @Getter
    private int totalPacks;

    @Getter
    private int openedPacks;

    @Getter
    private boolean isPackOpen = false;

    @Inject
    public CardManager(JsonManager jsonManager) {
        this.jsonManager = jsonManager;

        CardManager cardManager = jsonManager.load("cardmanager.json", CardManager.class);
        if (cardManager != null) {
            this.totalPacks = cardManager.totalPacks;
            this.openedPacks = cardManager.openedPacks;
            this.availablePacks = cardManager.availablePacks;
            this.heldCards = cardManager.heldCards;
        }
        else {
            this.totalPacks = 0;
            this.openedPacks = 0;
            this.availablePacks = 0;
        }
        this.allCards = jsonManager.load("allCards.json", new TypeToken<List<Card>>(){}.getType());
    }

    public CardManager(JsonManager jsonManager, int totalPacks, int openedPacks, int availablePacks) {
        this.jsonManager = jsonManager;
        this.totalPacks = totalPacks;
        this.openedPacks = openedPacks;
        this.availablePacks = availablePacks;
    }

    private void save() {
        jsonManager.save("cardmanager.json", this);
    }

    private void addAndSavePack() {
        this.availablePacks++;
        this.save();
    }

    private void removeAndSavePack() {
        this.availablePacks--;
        this.save();
    }

    // Add 3 random cards to be displayed in an overlay
    private void addOverlayCards() {
        // Use this to clear any overlayCards that may be left over
        this.overlayCards.clear();

        Random random = new Random();

        // Push 3 new overlay cards to the array
        for (int i = 0; i < 3; i++) {
            int randInt = random.nextInt(this.allCards.size()-1);
            Card randCard = this.allCards.get(randInt);
            this.overlayCards.add(randCard);
        }
    }

    // This method is used when the total pack needs to increase
    public void addAvailablePack() {
        this.totalPacks++;
        this.addAndSavePack();
    }

    // The method that is used by the "frontend" to "open" a pack of cards
    public void openPack() {
        this.addOverlayCards();
        this.isPackOpen = true;
    }

    // The method that is used by the "frontend" to "close" a pack of cards
    public void closePack() {
        this.overlayCards.clear();
        this.isPackOpen = false;
    }

    public void completePackOpening() {
        this.overlayCards.clear();
        this.isPackOpen = false;
        this.openedPacks++;
        this.removeAndSavePack();
    }

    // Add a card to the user's held cards
    public void selectCard(Card card) {
        if (card != null) {
            this.heldCards.add(card);
            this.save();
        }
    }

    public void displaySingleCard(Card card) {
        // If trying to display the same card again, just remove it from the UI instead
        if (!overlayCards.isEmpty() && overlayCards.get(0) == card) {
            this.overlayCards.clear();
        }
        // If trying to display a different card, add it to the UI
        else {
            this.overlayCards.clear();
            this.overlayCards.add(card);
        }
    }
}
