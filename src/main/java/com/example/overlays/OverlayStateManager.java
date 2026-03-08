package com.example.overlays;

import com.example.cards.Card;
import lombok.Getter;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class OverlayStateManager {
    private final int MAX_CARDS_PER_PAGE = 10;

    @Getter
    private boolean windowOpen = false;

    @Getter
    private int currentPage = 1;

    private final List<Card> overlayCards = new ArrayList<>();

    @Getter
    private final List<Card> paginatedCards = new ArrayList<>();

    @Getter
    private OverlayComponent overlayComponent = OverlayComponent.None;

    public enum OverlayComponent {
        None,
        AvailableCards,
        SingleCard,
        HeldCards,
        PackOpening
    }

    public void openWindow() {
        windowOpen = true;
    }

    public void closeOverlay() {
        windowOpen = false;
        overlayComponent = OverlayComponent.None;
        currentPage = 1;
        clearOverlayCards();
    }

    public void openOverlay(OverlayComponent component) {
        openWindow();
        currentPage = 1;
        overlayComponent = component;
    }

    public void openOverlay(OverlayComponent component, Card card) {
        openWindow();
        currentPage = 1;
        overlayComponent = component;
        addOverlayCards(card);
    }

    public void openOverlay(OverlayComponent component, List<Card> cards) {
        openWindow();
        currentPage = 1;
        overlayComponent = component;
        addOverlayCards(cards);
    }

    public boolean isNoneOpen() {
        return this.overlayComponent == OverlayComponent.None;
    }

    public boolean isAvailableCardsOpen() {
        return this.overlayComponent == OverlayComponent.AvailableCards;
    }

    public boolean isHeldCardsOpen() {
        return this.overlayComponent == OverlayComponent.HeldCards;
    }

    public boolean isSingleCardOpen() {
        return this.overlayComponent == OverlayComponent.SingleCard;
    }

    public boolean isPackOpeningOpen() {
        return this.overlayComponent == OverlayComponent.PackOpening;
    }

    // Add the cards that should be displayed
    public void addOverlayCards(Card card) {
        // Use this to clear any overlayCards that may be left over
        clearOverlayCards();

        // Reset the page back to 1
        resetPage();

        // Add them to the overlay
        this.overlayCards.add(card);

        this.paginateCards();
    }

    // Add the cards that should be displayed
    public void addOverlayCards(List<Card> cards) {
        // Use this to clear any overlayCards that may be left over
        clearOverlayCards();

        // Reset the page back to 1
        resetPage();

        // Add them to the overlay
        this.overlayCards.addAll(cards);
        this.paginateCards();
    }

    public void clearOverlayCards() {
        this.overlayCards.clear();
        this.paginatedCards.clear();
    }

    private void paginateCards() {
        this.paginatedCards.clear();

        int startingIndex = (currentPage-1) * MAX_CARDS_PER_PAGE; // 0 for first page if cards per page is 20
        int endingIndex = (currentPage * MAX_CARDS_PER_PAGE); // 20 for first page if cards per page is 20

        if (endingIndex >= overlayCards.size())
            endingIndex = overlayCards.size();

        this.paginatedCards.addAll(overlayCards.subList(startingIndex, endingIndex));
    }

    public int getTotalPages() {
        return (this.overlayCards.size() - 1) / MAX_CARDS_PER_PAGE + 1;
    }

    public void setCurrentPage(int page) {
        currentPage = page;
        this.paginateCards();
    }

    private void resetPage() {
        currentPage = 1;
    }

    public List<Card> getOverlayCards() {
        return this.paginatedCards;
    }
}