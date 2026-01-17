package com.example.cards;

import lombok.Getter;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

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

    public CardManager() {
        this.totalPacks = 0;
        this.openedPacks = 0;
    }

    public CardManager(int totalPacks, int openedPacks, List<Card> cards) {
        this.totalPacks = totalPacks;
        this.openedPacks = openedPacks;
        this.cards = cards;
    }

    public void addAvailablePack() {
        this.availablePacks++;
        CardReaderWriter.writeCardManager(this);
    }

    public void removeAvailablePack() {
        this.availablePacks--;
        CardReaderWriter.writeCardManager(this);
    }

    // This method is used when the total pack needs to increase
    public void awardNewPack() {
        this.totalPacks++;
        this.addAvailablePack();
    }

    public void openPack() {
        this.openedPacks++;
        this.removeAvailablePack();
    }

//    public List<Card>
}
