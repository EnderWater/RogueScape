package com.example.cards;

import com.example.widgets.WidgetManager;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
public class CardManager {
    @Inject
    private WidgetManager widgetManager;

    @Getter
    private List<Card> allCards = new ArrayList<>();

    // This list contains all the card ids that the user currently has.
    @Getter
    private List<Integer> heldCardIds;

    @Getter
    private int availablePacks;

    @Getter
    private int totalPacks;

    @Getter
    private int openedPacks;

    @Inject
    public CardManager(WidgetManager widgetManager) {
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

        this.widgetManager = widgetManager;
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
//        Random random = new Random();
//        random.nextInt(this.cards.size()-1);

        this.widgetManager.openWidget();

        this.openedPacks++;
        this.removeAndSavePack();
    }
}
