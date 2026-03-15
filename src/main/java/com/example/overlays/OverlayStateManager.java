package com.example.overlays;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class OverlayStateManager {

    private int MAX_ITEMS_PER_PAGE = 0;

    @Getter
    private boolean windowOpen = false;

    @Getter
    private int currentPage = 1;

    @Getter
    private final List<?> overlayItems = new ArrayList<>();

    @Getter
    private final List<?> paginatedItems = new ArrayList<>();

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

    public <T> List<T> getOverlayItems() {
        return (List<T>)overlayItems;
    }

    public <T> List<T> getPaginatedItems() {
        return (List<T>)paginatedItems;
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

    public <T> void openOverlay(OverlayComponent component, T item, int maxItemsPerPage) {
        openWindow();
        currentPage = 1;
        overlayComponent = component;
        MAX_ITEMS_PER_PAGE = maxItemsPerPage;
        addOverlayItems(item);
    }

    public <T> void openOverlay(OverlayComponent component, List<T> items,  int maxItemsPerPage) {
        openWindow();
        resetPage();

        overlayComponent = component;
        MAX_ITEMS_PER_PAGE = maxItemsPerPage;

        addOverlayItems(items);
    }

    public <T> void addOverlayItems(T item) {
        // Use this to clear any overlayCards that may be left over
        clearOverlay();

        // Reset the page back to 1
        resetPage();

        // Add them to the overlay
        ((List<T>)this.overlayItems).add(item);
        this.paginate();
    }

    public <T> void addOverlayItems(List<T> items) {
        // Use this to clear any overlayCards that may be left over
        clearOverlay();

        // Reset the page back to 1
        resetPage();

        // Add them to the overlay
        ((List<T>)this.overlayItems).addAll(items);
        this.paginate();
    }

    public void clearOverlay() {
        this.overlayItems.clear();
        this.paginatedItems.clear();
    }


    private <T> void paginate() {
        this.paginatedItems.clear();

        int startingIndex = (currentPage - 1) * MAX_ITEMS_PER_PAGE; // 0 for first page if cards per page is 20
        int endingIndex = (currentPage * MAX_ITEMS_PER_PAGE); // 20 for first page if cards per page is 20

        if (endingIndex >= overlayItems.size())
            endingIndex = overlayItems.size();

        List<T> overlayItems = (List<T>)this.overlayItems;
        ((List<T>)this.paginatedItems).addAll(overlayItems.subList(startingIndex, endingIndex));
    }

    public int getTotalPages() {
        int totalPages = 0;

        if (!overlayItems.isEmpty())
            totalPages = (this.overlayItems.size() - 1) / MAX_ITEMS_PER_PAGE + 1;

        return totalPages;
    }

    public void setCurrentPage(int page) {
        currentPage = page;
        this.paginate();
    }

    private void resetPage() {
        currentPage = 1;
    }
}