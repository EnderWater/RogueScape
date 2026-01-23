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
    @Getter
    private int availablePacks;

    @Getter
    private List<Pack> packs; // I probably don't need to load packs... I need to load cards
    // and then place them into packs randomly based on their rarity

    @Getter
    private List<Card> cards = new ArrayList<>();

    private int totalPacks;
    private int openedPacks;
    @Inject
    private WidgetManager widgetManager;

    public CardManager() {
        this.totalPacks = 0;
        this.openedPacks = 0;
    }

    @Inject
    public CardManager(int totalPacks, int openedPacks, List<Card> cards, WidgetManager widgetManager) {
        this.totalPacks = totalPacks;
        this.openedPacks = openedPacks;
        this.cards = cards;
        this.widgetManager = widgetManager;
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
