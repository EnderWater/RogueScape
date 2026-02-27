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
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class CardManager {
    private final JsonManager jsonManager;
    private List<Card> allCards = new ArrayList<>();
    private List<Card> availableCards = new ArrayList<>();

    // This list contains all the cards that the user currently has.
    @Getter
    private List<Card> heldCards = new ArrayList<>();

    // This list contains the current 3-5 cards that are visible on the screen while the user is choosing
    @Getter
    private List<Card> overlayCards = new ArrayList<>();

    // This list contains the cards from a previously opened pack. If a user closes the pack before selecting a card,
    // the same cards will appear again when they open a pack again
    @Getter
    private List<Card> openedPackCards = new ArrayList<>();

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
            this.openedPackCards = cardManager.openedPackCards;
        }
        else {
            this.totalPacks = 0;
            this.openedPacks = 0;
            this.availablePacks = 0;
        }
        this.allCards = jsonManager.load("allCards.json", new TypeToken<List<Card>>(){}.getType());
        Set<Integer> heldIds = this.heldCards.stream()
                .map(Card::getCardId)
                .collect(Collectors.toSet());

        this.availableCards = this.allCards.stream()
                .filter(card -> !heldIds.contains(card.getCardId()))
                .collect(Collectors.toList());
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

        if (!this.openedPackCards.isEmpty()) {
            this.overlayCards.addAll(this.openedPackCards);
            return;
        }

        Random random = new Random();

        int i = 0;
        while (i < 3) {
            int randInt = random.nextInt(this.availableCards.size()-1);
            Card randCard = this.availableCards.get(randInt);

            // If the selected card is already in the user's hand somehow
            if (this.heldCards.contains(randCard))
                continue;

            // This will help the app "remember" which cards were used during the last opening
            this.openedPackCards.add(randCard);
            i++;
        }
        // Save the openedPackCards after they've been set
        this.save();

        // Add them to the overlay
        this.overlayCards.addAll(this.openedPackCards);
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
            this.availableCards.remove(card);
            this.openedPackCards.clear();
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
