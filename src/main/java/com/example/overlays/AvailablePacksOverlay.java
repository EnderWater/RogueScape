package com.example.overlays;

import com.example.cards.Card;
import com.example.cards.CardManager;
import com.example.packs.Pack;
import com.example.packs.PackManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class AvailablePacksOverlay {
    private static final int SIDEBAR_WIDTH = 140;
    private static final int SIDEBAR_ROW_HEIGHT = 20;

    private static final int COLUMNS = 3;
    private static final int ROWS = 2;
    private static final int SPACING = 30;

    private final CardManager cardManager;
    private final OverlayStateManager overlayStateManager;
    private final PackRenderer packRenderer;
    private final PackManager packManager;

    private final List<Rectangle> packBounds = new ArrayList<>();
    private final List<Rectangle> sidebarBounds = new ArrayList<>();

    @Inject
    public AvailablePacksOverlay(CardManager cardManager, OverlayStateManager overlayStateManager, PackRenderer packRenderer, PackManager packManager) {
        this.cardManager = cardManager;
        this.overlayStateManager = overlayStateManager;
        this.packRenderer = packRenderer;
        this.packManager = packManager;
    }

    public void render(Graphics2D graphics, int containerX, int containerY, int containerWidth, int containerHeight) {
        List<Pack> packs = overlayStateManager.getPaginatedItems();
        List<String> packCategories = packManager.getOverlayPackNames();

        if (packs == null || !overlayStateManager.isAllPacksOpen()) {
            return;
        }

        packBounds.clear();
        sidebarBounds.clear();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        renderSidebar(graphics, containerX, containerY, packCategories);

        renderPackGrid(graphics, containerX + SIDEBAR_WIDTH, containerY, containerWidth - SIDEBAR_WIDTH, containerHeight, packs);
    }

    private void renderSidebar(Graphics2D graphics, int containerX, int containerY, List<String> categories) {
        if (categories == null) {
            return;
        }

        for (int i = 0; i < categories.size(); i++) {
            int y = containerY + i * SIDEBAR_ROW_HEIGHT;

            Rectangle rowBounds = new Rectangle(
                    containerX,
                    y,
                    SIDEBAR_WIDTH,
                    SIDEBAR_ROW_HEIGHT
            );

            sidebarBounds.add(rowBounds);

            graphics.setColor(Color.DARK_GRAY);
            graphics.fill(rowBounds);

            graphics.setColor(Color.WHITE);
            graphics.drawString(
                    categories.get(i),
                    containerX + 6,
                    y + 14
            );
        }
    }

    private void renderPackGrid(Graphics2D graphics, int x, int y, int width, int height, List<Pack> packs) {
        if (packs.isEmpty()) {
            return;
        }

        int packWidth = (int) (width * 0.18);
        int packHeight = (int) (height * 0.35);

        int totalRows = Math.min(ROWS, (int) Math.ceil((double) packs.size() / COLUMNS));

        int gridHeight = (totalRows * packHeight) + ((totalRows - 1) * SPACING);
        int startY = y + (height - gridHeight) / 2;

        for (int i = 0; i < packs.size() && i < 6; i++) {
            int row = i / COLUMNS;
            int col = i % COLUMNS;

            int packsInRow = Math.min(COLUMNS, packs.size() - (row * COLUMNS));
            int rowWidth = (packsInRow * packWidth) + ((packsInRow - 1) * SPACING);

            int rowStartX = x + (width - rowWidth) / 2;

            int drawX = rowStartX + col * (packWidth + SPACING);
            int drawY = startY + row * (packHeight + SPACING);

            Rectangle bounds = packRenderer.renderPack(graphics, packs.get(i), drawX, drawY, packWidth, packHeight);

            packBounds.add(bounds);
        }
    }

    public boolean handleClick(MouseEvent event) {
        // If neither the availablePacks nor the cards in pack component are open, don't let them consume the event
        if (!overlayStateManager.isAllPacksOpen() && !overlayStateManager.isPackCardsOpen())
            return false;

        Rectangle sidebarClick = isClickOnSidebar(event);

        if (sidebarClick != null) {
            int index = sidebarBounds.indexOf(sidebarClick);
            packBounds.clear();
            sidebarBounds.clear();
            String clickedPackName = packManager.getOverlayPackNames().get(index);
            List<Card> heldCards = cardManager.getHeldCards();

            List<Card> cards = cardManager.getAvailableCards().stream()
                    .filter(card -> Objects.equals(card.getPackName(), clickedPackName) && !heldCards.contains(card))
                    .collect(Collectors.toList());

//            overlayStateManager.selectPackName(index, cards);
//            String packName = this.overlayPackNames.get(index);

            // Once we have the name, we need to change the view to a card view and add specific cards to a view
//            overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.PackAvailableCards, cards, 10);
            this.cardManager.openFilteredCardsOverlay(cards);
            return true;
        }

        Rectangle packClick = isClickOnPack(event);

        if (packClick != null) {
            Pack pack = getPackFromBounds(packClick);
            // Don't let the user open a pack if they don't have the currency
            if (pack.getAvailable() <= 0)
                return true;

            packBounds.clear();
            sidebarBounds.clear();

            // Open the pack and let the cardManager handle opening the overlay
            this.cardManager.openPackOverlay(pack.getName());
            return true;
        }

        return false;
    }

    private Pack getPackFromBounds(Rectangle bounds) {
        int index = packBounds.indexOf(bounds);
        return overlayStateManager.<Pack>getPaginatedItems().get(index);
    }

    private Rectangle isClickOnPack(MouseEvent event) {
        for (Rectangle bounds : packBounds) {
            if (bounds.contains(event.getPoint())) {
                return bounds;
            }
        }
        return null;
    }

    private Rectangle isClickOnSidebar(MouseEvent event) {
        for (Rectangle bounds : sidebarBounds) {
            if (bounds.contains(event.getPoint())) {
                return bounds;
            }
        }
        return null;
    }
}