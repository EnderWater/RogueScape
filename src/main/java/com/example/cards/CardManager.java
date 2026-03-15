package com.example.cards;

import com.example.JsonManager;
import com.example.listeners.CardChangeListener;
import com.example.overlays.OverlayStateManager;
import com.example.packs.PackManager;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class CardManager {
    private final JsonManager jsonManager;
    private final PackManager packManager;
    private final OverlayStateManager overlayStateManager;

    private final int MAX_CARDS_PER_PAGE = 10;

    // This list contains all the cards that the user does not have
    @Getter
    private List<Card> availableCards;

    // This list contains all the cards that the user currently has.
    @Getter
    private List<Card> heldCards = new ArrayList<>();

    // This map contains the cards from a previously opened pack. If a user closes the pack before selecting a card,
    // the same cards will appear again when they open a pack again
    @Getter
    private Map<String, List<Card>> openedPackCards = new HashMap<>();

    // This map contains a list of all cards of a region where the region name is the key and the list is the value
    @Getter
    private Map<String, List<Card>> regionCards = new HashMap<>();

    private final List<CardChangeListener> listeners = new ArrayList<>();

    public void addListener(CardChangeListener listener) {
        listeners.add(listener);
    }

    @Inject
    public CardManager(JsonManager jsonManager, PackManager packManager, OverlayStateManager overlayStateManager) {
        this.jsonManager = jsonManager;
        this.packManager = packManager;
        this.overlayStateManager = overlayStateManager;

        CardManager cardManager = jsonManager.load("cardmanager.json", CardManager.class);
        if (cardManager != null) {
            this.heldCards = cardManager.heldCards;
            this.openedPackCards = cardManager.openedPackCards;
        }

        List<Card> allCards = CardCsvLoader.read(Paths.get("plugins", "roguescape", "allCards.csv"));

        Set<Integer> heldIds = this.heldCards.stream()
                .map(Card::getCardId)
                .collect(Collectors.toSet());

        this.availableCards = allCards.stream()
                .filter(card -> !heldIds.contains(card.getCardId()))
                .collect(Collectors.toList());
    }

    private void save() {
        jsonManager.save("cardmanager.json", this);
    }

    // Add a card to the user's held cards
    public void selectCard(Card card) {
        if (card != null) {
            this.addCard(card);

            this.openedPackCards.clear();

            // Since a card was chosen, increment the opened packs and reduce the available packs
            this.packManager.addOpenedPacks(card.getPackName());

            // Save your work! :)
            this.save();
        }
    }

    public void addCard(Card card) {
        if (card != null) {
            this.heldCards.add(card);
            this.availableCards.remove(card);

            this.save();
            this.notifyListeners();
        }
    }

    public void deleteHeldCard(Card card) {
        this.heldCards.remove(card);
        this.availableCards.add(card);
        this.save();
        this.notifyListeners();
    }

    public void deleteAllHeldCards() {
        this.availableCards.addAll(this.heldCards);
        this.heldCards.clear();
        this.save();
        this.notifyListeners();
    }

    private void notifyListeners() {
        for (CardChangeListener listener : listeners) {
            listener.onCardsChanged();
        }
    }

    public void saveCards() {
        save();
    }

    public void openPackOverlay(String packName) {
        if (packName.isBlank()) return;

        List<Card> regionOpenedPackCards = this.openedPackCards.get(packName);

        if (regionOpenedPackCards != null && !regionOpenedPackCards.isEmpty()) {
            this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.PackOpening, regionOpenedPackCards, 6);
            return;
        }

        // Check to see if an entry in the map for the packName exists. If not, create it.
        if (!openedPackCards.containsKey(packName))
            openedPackCards.put(packName, new ArrayList<>());

        // If the region hasn't had a pack opened already, open a new one
        List<Card> regionPackCards = this.availableCards.stream()
                .filter(card -> Objects.equals(card.getPackName(), packName))
                .collect(Collectors.toList());

        Random random = new Random();

        int i = 0;

        // If there are less than 5 cards, just add them all and skip the while loop
        if (regionPackCards.size() < 5) {
            this.openedPackCards.get(packName).addAll(regionPackCards);
            i = 5;
        }

        while (i < 5) {
            int randInt = random.nextInt(regionPackCards.size()-1);
            Card randCard = regionPackCards.get(randInt);

            // If the selected card is already in the user's hand somehow
            if (this.heldCards.contains(randCard) || this.openedPackCards.get(packName).contains(randCard))
                continue;

            // This will help the app "remember" which cards were used during the last opening
            this.openedPackCards.get(packName).add(randCard);
            i++;
        }
        // Save the openedPackCards after they've been set
        this.save();

        List<Card> packCards = this.openedPackCards.get(packName);
        this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.PackOpening, packCards, MAX_CARDS_PER_PAGE);
    }

    public void openAvailableCardsOverlay() {
        this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.AvailableCards, availableCards, MAX_CARDS_PER_PAGE);
    }

    public void openHeldCardsOverlay() {
        this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.HeldCards, heldCards, MAX_CARDS_PER_PAGE);
    }

    public void openSingleCardOverlay(Card card) {
        this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.SingleCard, card, MAX_CARDS_PER_PAGE);
    }

    public void openFilteredCardsOverlay(List<Card> cards) {
        this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.FilteredCards, cards, MAX_CARDS_PER_PAGE);
    }
}
