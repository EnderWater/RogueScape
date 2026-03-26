package com.example.overlays;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class OverlayStateManager {

    private int MAX_ITEMS_PER_PAGE = 0;

    @Getter
    private boolean windowOpen = false;

    @Getter
    private int currentPage = 1;

    @Getter
    private final List<OverlayItem> overlayItems = new ArrayList<>();

    @Getter
    private final List<OverlayItem> filteredOverlayItems = new ArrayList<>();

    @Getter
    private final List<OverlayItem> paginatedItems = new ArrayList<>();

    @Getter
    private OverlayComponent overlayComponent = OverlayComponent.None;

    public enum OverlayComponent {
        None,
        AvailableCards,
        SingleCard,
        HeldCards,
        PackOpening,
        AllPacks,
        FilteredCards,
        AllTasks,
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
        return this.overlayComponent == OverlayComponent.FilteredCards;
    }

    public boolean isAllTasksOpen() {
        return this.overlayComponent == OverlayComponent.AllTasks;
    }

    @Inject
    public OverlayStateManager() {}

    public void openWindow() {
        windowOpen = true;
    }

    public void closeOverlay() {
        windowOpen = false;
        overlayComponent = OverlayComponent.None;
        currentPage = 1;
        clearOverlay();
    }

    public void openOverlay(OverlayComponent component, OverlayItem item, int maxItemsPerPage) {
        openWindow();
        currentPage = 1;
        overlayComponent = component;
        MAX_ITEMS_PER_PAGE = maxItemsPerPage;
        addOverlayItems(item);
    }

    public void openOverlay(OverlayComponent component, List<? extends OverlayItem> items,  int maxItemsPerPage) {
        openWindow();
        resetPage();

        overlayComponent = component;
        MAX_ITEMS_PER_PAGE = maxItemsPerPage;

        addOverlayItems(items);
    }

    public void addOverlayItems(OverlayItem item) {
        // Use this to clear any overlayCards that may be left over
        clearOverlay();

        // Reset the page back to 1
        resetPage();

        // Add them to the overlay
        this.overlayItems.add(item);
        this.filteredOverlayItems.add(item);
        this.paginate();
    }

    public void addOverlayItems(List<? extends OverlayItem> items) {
        // Use this to clear any overlayCards that may be left over
        clearOverlay();

        // Reset the page back to 1
        resetPage();

        // Add them to the overlay
        this.overlayItems.addAll(items);
        this.filteredOverlayItems.addAll(items);
        this.paginate();
    }

    public void clearOverlay() {
        this.overlayItems.clear();
        this.filteredOverlayItems.clear();
        this.paginatedItems.clear();
    }


    private void paginate() {
        this.paginatedItems.clear();

        int startingIndex = (currentPage - 1) * MAX_ITEMS_PER_PAGE; // 0 for first page if cards per page is 20
        int endingIndex = (currentPage * MAX_ITEMS_PER_PAGE); // 20 for first page if cards per page is 20

        if (endingIndex >= filteredOverlayItems.size())
            endingIndex = filteredOverlayItems.size();

        List<OverlayItem> filteredOverlayItems = this.filteredOverlayItems;
        this.paginatedItems.addAll(filteredOverlayItems.subList(startingIndex, endingIndex));
    }

    public int getTotalPages() {
        int totalPages = 0;

        if (!this.filteredOverlayItems.isEmpty())
            totalPages = (this.filteredOverlayItems.size() - 1) / MAX_ITEMS_PER_PAGE + 1;

        return totalPages;
    }

    public void setCurrentPage(int page) {
        currentPage = page;
        this.paginate();
    }

    private void resetPage() {
        currentPage = 1;
    }

    public void searchAndUpdateOverlayItems(String searchString) {
        String lowercaseSearch = searchString.toLowerCase();
        List<OverlayItem> searchedItems = this.overlayItems
                .stream()
                .filter(item -> item.getSearchableString().toLowerCase().contains(lowercaseSearch))
                .collect(Collectors.toList());

        this.filteredOverlayItems.clear();
        this.filteredOverlayItems.addAll(searchedItems);
        this.paginate();
    }
}
