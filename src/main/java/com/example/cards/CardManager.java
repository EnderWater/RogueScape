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

/**
 * This class manages all cards throughout the plugin. It stores every held card and every available card. It also handles
 * all card related overlay interactions with the OverlayStateManager.
 */
@Singleton
public class CardManager {
    private final JsonManager jsonManager;
    private final PackManager packManager;
    private final OverlayStateManager overlayStateManager;

    // This controls the max number of cards that can be shown per page
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

    // Stores the listeners
    private final List<CardChangeListener> listeners = new ArrayList<>();

    // A public method that allows any class that injects CardManager to listen to changes
    public void addListener(CardChangeListener listener) {
        listeners.add(listener);
    }

    // Injects everything the CardManager needs and loads previous CardManager state from disk. If no previous state was
    // found, load the default and build CardManager from the ground up.
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

    // Save the CardManager's current state
    private void save() {
        jsonManager.save("cardmanager.json", this);
    }

    // Add a card to the user's held cards
    public void selectCard(Card card) {
        if (card != null) {
            // If the card is a goal card, intercept the normal workflow and instead run the goal card (pack technically) workflow
            if (card instanceof GoalCard) {
                this.selectGoalCard((GoalCard)card);
                return;
            }

            this.addCard(card);

            this.openedPackCards.clear();

            // Since a card was chosen, increment the opened packs and reduce the available packs
            this.packManager.addOpenedPacks(card.getPackName());

            // Save your work! :)
            this.save();
        }
    }

    // Select a goal card - this opens a special set of cards to choose from
    public void selectGoalCard(GoalCard card) {
        String packName = card.getPackName();
        List<Card> goalCards = this.getRandomCards(packName, 3);

    }

    // Add a single card to the user's held cards
    public void addCard(Card card) {
        if (card != null) {
            this.heldCards.add(card);
            this.availableCards.remove(card);

            this.save();
            this.notifyListeners();
        }
    }

    // Deletes one held card
    public void deleteHeldCard(Card card) {
        this.heldCards.remove(card);
        this.availableCards.add(card);
        this.save();
        this.notifyListeners();
    }

    // Deletes all held cards (shocker)
    public void deleteAllHeldCards() {
        this.availableCards.addAll(this.heldCards);
        this.heldCards.clear();
        this.save();
        this.notifyListeners();
    }

    // This method notifies any listeners to let them know changes happened to the CardManager
    private void notifyListeners() {
        for (CardChangeListener listener : listeners) {
            listener.onCardsChanged();
        }
    }

    // A public method for anything injecting CardManager to save the current state of the CardManager
    public void saveCards() {
        save();
    }

    // Open a pack of cards for the given packName.
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

        List<Card> randomCards = getRandomCards(packName, 5);
        this.openedPackCards.get(packName).addAll(randomCards);

        // Save the openedPackCards after they've been set
        this.save();

        List<Card> packCards = this.openedPackCards.get(packName);
        this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.PackOpening, packCards, MAX_CARDS_PER_PAGE);
    }

    // This method will grab 5 random cards from the given packName and return them in a list
    private List<Card> getRandomCards(String packName, int numCards) {
        List<Card> returnCards = new ArrayList<>();

        // If the region hasn't had a pack opened already, open a new one
        List<Card> regionPackCards = this.availableCards.stream()
                .filter(card -> Objects.equals(card.getPackName(), packName))
                .collect(Collectors.toList());

        Random random = new Random();

        // If there are less than or equal to numCards, just add them all and skip the while loop
        if (regionPackCards.size() <= numCards) {
            return regionPackCards;
        }

        int i = 0;
        int retry = 0;

        // Loop x times and select x random cards. If a new card isn't found within 100 retries, stop the loop and return what was found
        while (i < numCards && retry < 100) {
            int randInt = random.nextInt(regionPackCards.size()-1);
            Card randCard = regionPackCards.get(randInt);

            // If the selected card is already in the user's hand somehow
            if (this.heldCards.contains(randCard) || returnCards.contains(randCard)) {
                retry++;
                continue;
            }

            // This will help the app "remember" which cards were used during the last opening
            returnCards.add(randCard);
            i++;
        }

        return returnCards;
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
