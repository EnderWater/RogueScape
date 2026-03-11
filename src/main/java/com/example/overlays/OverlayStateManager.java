package com.example.overlays;

import com.example.cards.Card;
import com.example.packs.Pack;
import com.example.packs.PackManager;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class OverlayStateManager {
    private final PackManager packManager;

    private final int MAX_CARDS_PER_PAGE = 10;

    @Getter
    private boolean windowOpen = false;

    @Getter
    private int currentPage = 1;

    @Getter
    private final List<Card> overlayCards = new ArrayList<>();

    @Getter
    private final List<Pack> overlayPacks = new ArrayList<>();

    @Getter
    private final List<Card> paginatedCards = new ArrayList<>();

    @Getter
    private final List<Pack> paginatedPacks = new ArrayList<>();

    @Getter
    private final List<String> overlayPackNames;

    @Getter
    private String currentPackName;

    @Getter
    private OverlayComponent overlayComponent = OverlayComponent.None;

    public enum OverlayComponent {
        None,
        AvailableCards,
        SingleCard,
        HeldCards,
        PackOpening,
        AllPacks,
        PackAvailableCards,
    }

    @Inject
    public OverlayStateManager(PackManager packManager) {
        this.packManager = packManager;
        this.overlayPackNames = this.packManager.getPackNameList().stream().sorted().collect(Collectors.toList());
    }

    public void openWindow() {
        windowOpen = true;
    }

    public void closeOverlay() {
        windowOpen = false;
        overlayComponent = OverlayComponent.None;
        currentPage = 1;
        clearOverlay();
    }

    public void openOverlay(OverlayComponent component) {
        openWindow();
        currentPage = 1;
        overlayComponent = component;
        clearOverlay();
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

    public void openPackOverlay(OverlayComponent component, List<Pack> packs) {
        openWindow();
        currentPage = 1;
        overlayComponent = component;
        addOverlayPacks(packs);
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

    public boolean isAllPacksOpen() {
        return this.overlayComponent == OverlayComponent.AllPacks;
    }

    public boolean isPackCardsOpen() {
        return this.overlayComponent == OverlayComponent.PackAvailableCards;
    }

    // Add the cards that should be displayed
    public void addOverlayCards(Card card) {
        // Use this to clear any overlayCards that may be left over
        clearOverlay();

        // Reset the page back to 1
        resetPage();

        // Add them to the overlay
        this.overlayCards.add(card);

        this.paginate();
    }

    // Add the cards that should be displayed
    public void addOverlayCards(List<Card> cards) {
        // Use this to clear any overlayCards that may be left over
        clearOverlay();

        // Reset the page back to 1
        resetPage();

        // Add them to the overlay
        this.overlayCards.addAll(cards);
        this.paginate();
    }

    public void clearOverlay() {
        // Clear card data
        this.overlayCards.clear();
        this.paginatedCards.clear();

        // Clear pack data
        this.overlayPacks.clear();
        this.paginatedPacks.clear();
    }

    // Add the cards that should be displayed
    public void addOverlayPacks(List<Pack> packs) {
        // Use this to clear any overlayCards that may be left over
        clearOverlay();

        // Reset the page back to 1
        resetPage();

        // Sort the cards
        packs.sort(Comparator.comparing(Pack::getName));

        // Add them to the overlay
        this.overlayPacks.addAll(packs);
        this.paginate();
    }

    private void paginate() {
        if (!overlayPacks.isEmpty())
            paginatePacks();
        else if (!overlayCards.isEmpty())
            paginateCards();
    }

    private void paginateCards() {
        this.paginatedCards.clear();

        int startingIndex = (currentPage - 1) * MAX_CARDS_PER_PAGE; // 0 for first page if cards per page is 20
        int endingIndex = (currentPage * MAX_CARDS_PER_PAGE); // 20 for first page if cards per page is 20

        if (endingIndex >= overlayCards.size())
            endingIndex = overlayCards.size();

        this.paginatedCards.addAll(overlayCards.subList(startingIndex, endingIndex));
    }

    private void paginatePacks() {
        this.paginatedPacks.clear();

        int startingIndex = (currentPage - 1) * 6; // 0 for first page if cards per page is 20
        int endingIndex = (currentPage * 6); // 20 for first page if cards per page is 20

        if (endingIndex >= overlayPacks.size())
            endingIndex = overlayPacks.size();

        this.paginatedPacks.addAll(overlayPacks.subList(startingIndex, endingIndex));
    }

    public int getTotalPages() {
        int totalPages;
        if (!overlayCards.isEmpty())
            totalPages = (this.overlayCards.size() - 1) / MAX_CARDS_PER_PAGE + 1;
        else if (!overlayPacks.isEmpty()) {
            totalPages = (this.overlayPacks.size() - 1) / MAX_CARDS_PER_PAGE + 1;
        }
        else
            totalPages = 0;

        return totalPages;
    }

    public void setCurrentPage(int page) {
        currentPage = page;
        this.paginate();
    }

    private void resetPage() {
        currentPage = 1;
    }

    public void selectPackName(int index, List<Card> cards) {
        String packName = this.overlayPackNames.get(index);

        // Once we have the name, we need to change the view to a card view and add specific cards to a view
        this.openOverlay(OverlayComponent.PackAvailableCards, cards);
    }
}