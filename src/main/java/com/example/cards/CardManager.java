package com.example.cards;

import com.example.JsonManager;
import com.example.listeners.CardChangeListener;
import com.example.overlays.OverlayItem;
import com.example.overlays.OverlayStateManager;
import com.example.packs.PackManager;
import com.example.tasks.GoalTask;
import com.example.tasks.TaskManager;
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
    private final TaskManager taskManager;
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

    // Stores the listeners
    private final List<CardChangeListener> listeners = new ArrayList<>();

    // A public method that allows any class that injects CardManager to listen to changes
    public void addListener(CardChangeListener listener) {
        listeners.add(listener);
    }

    // Injects everything the CardManager needs and loads previous CardManager state from disk. If no previous state was
    // found, load the default and build CardManager from the ground up.
    @Inject
    public CardManager(JsonManager jsonManager, PackManager packManager, OverlayStateManager overlayStateManager, TaskManager taskManager) {
        this.jsonManager = jsonManager;
        this.packManager = packManager;
        this.overlayStateManager = overlayStateManager;
        this.taskManager = taskManager;

        CardManager cardManager = jsonManager.load("cardmanager.json", CardManager.class);
        if (cardManager != null) {
            this.heldCards = cardManager.heldCards;
            this.openedPackCards = cardManager.openedPackCards;
        }

        List<Card> allCards = CardCsvLoader.read(Paths.get("assets", "data", "allCards.csv"));

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
    public boolean selectCard(Card card) {
        if (card != null) {
            // If the card is a goal pack, intercept the normal workflow and instead run the goal pack workflow
            // A card is a goal pack if it is a GoalCard object with an id of -8 because it's my favorite number :)
            if (card instanceof GoalCard && card.getCardId() == -8) {
                this.selectGoalCard((GoalCard) card);
                return false;
            }

            // If the card is a goal card, a special task needs to be created since the user is selecting it
            if (card instanceof GoalCard) {
//                GoalTask(String taskType, String taskName, String description, int current, int target, boolean isPinned, int packsAwarded)
                GoalTask goalTask = new GoalTask("Goal", card.getName(), card.getDescription(), 0, 1, true, ((GoalCard) card).getPacksAwarded());
                // Add the new task
                taskManager.addTask(goalTask);
            }

            this.addCard(card);

            // Clear out the openedPackCards for the given packName
            this.openedPackCards.get(card.getPackName()).clear();

            // Since a card was chosen, increment the opened packs and reduce the available packs
            this.packManager.completePackOpening(card.getPackName(), 1, 1);

            // Save your work! :)
            this.save();

            return true;
        }
        return false;
    }

    // Select a goal card - this opens a special set of cards to choose from
    public void selectGoalCard(GoalCard card) {
        String packName = card.getPackName();

        // goalCards contains cards that are in the same pack, with a rarity of special, that are not already held.
        List<Card> goalCards = this.getAvailableCards().stream()
                .filter(c -> Objects.equals(c.getPackName(), packName) && c.getRarity() == CardRarity.Special && !heldCards.contains(c))
                .collect(Collectors.toList());

        List<Card> randomCards = getRandomCards(goalCards, packName, 3);
        this.openedPackCards.get(packName).clear();
        this.openedPackCards.get(packName).addAll(randomCards);

        // Save the openedPackCards after they've been set
        this.save();

        List<Card> packCards = this.openedPackCards.get(packName);
        this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.PackOpening, packCards, MAX_CARDS_PER_PAGE);
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
            this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.PackOpening, regionOpenedPackCards, 5);
            return;
        }

        // Check to see if an entry in the map for the packName exists. If not, create it.
        if (!openedPackCards.containsKey(packName))
            openedPackCards.put(packName, new ArrayList<>());

        List<Card> randomCards = getRandomCards(this.availableCards, packName, 5);
        // Replace any GoalCards found with default GoalCards (which are called Goal Packs)
        randomCards.replaceAll(this::replaceIfGoalCard);
        this.openedPackCards.get(packName).addAll(randomCards);

        // Save the openedPackCards after they've been set
        this.save();

        List<Card> packCards = this.openedPackCards.get(packName);
        this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.PackOpening, packCards, MAX_CARDS_PER_PAGE);
    }

    // This method will grab numCards random cards from the given packName and return them in a list
    private List<Card> getRandomCards(List<Card> cards, String packName, int numCards) {
        List<Card> returnCards = new ArrayList<>();

        // Get the list of cards with the same packName
        List<Card> regionPackCards = cards.stream()
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
            int randInt = random.nextInt(regionPackCards.size() - 1);
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

    /*
      This is a strange method. In order to facilitate goal packs, there are x number of goal cards per region pack.
      If one of those goal cards appears in the random card choice, we're not going to let the user select it by itself.
      Instead, clicking it should open another card view where the user will be able to select 3 different types of goals.
      Essentially, the user gets a choice of multiple goal cards if they roll one. So, if a goal card is rolled, set up a
      new GoalCard instance that will be part of the list. Clicking on this card as a choice will run the selectGoalCard()
      method in CardManager. Check the logic there for further details about what happens after selecting the new GoalCard.
     */
    private Card replaceIfGoalCard(Card card) {
        if (card instanceof GoalCard) {
            return new GoalCard(-8, "Goal Pack", "Open a Goal Pack", "", CardRarity.Special, "Goal", card.getPackName(), 0);
        }
        return card;
    }
}
