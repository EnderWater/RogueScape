package com.example.overlays;

import com.example.cards.*;
import com.example.packs.PackManager;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

@Singleton
public class CardListOverlay {
    private final double CARD_WIDTH_PERCENTAGE = 0.15;
    private final double CARD_HEIGHT_PERCENTAGE = 0.47;
    private final int SPACING = 12;

    private final CardManager cardManager;
    private final OverlayStateManager overlayStateManager;
    private final PackManager packManager;
    private final CardRenderer cardRenderer;
    private final List<Rectangle> cardBounds = new ArrayList<>();

    @Inject
    public CardListOverlay(CardManager cardManager, OverlayStateManager overlayStateManager, CardRenderer cardRenderer, PackManager packManager) {
        this.cardManager = cardManager;
        this.overlayStateManager = overlayStateManager;
        this.cardRenderer = cardRenderer;
        this.packManager = packManager;
    }

    /**
     * Render cards inside a container.
     * Container size is provided by ContainerOverlay.
     */
    public void render(Graphics2D graphics, int containerX, int containerY, int containerWidth, int containerHeight) {
        List<Card> cards = overlayStateManager.getPaginatedItems();

        if (cards == null || cards.isEmpty()) {
            return;
        }

        cardBounds.clear();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cardWidth = (int) (containerWidth * CARD_WIDTH_PERCENTAGE);
        int cardHeight = (int) (containerHeight * CARD_HEIGHT_PERCENTAGE);

        int COLUMNS = 5;
        int ROWS = Math.min((int)Math.ceil((double) cards.size() / 5), 2);

        if (ROWS == 1) {
            cardHeight = (int) (containerHeight * .60);
        }

        int gridHeight = (ROWS * cardHeight) + ((ROWS - 1) * SPACING);
        int startY = (containerHeight - gridHeight) / 2;

        for (int i = 0; i < cards.size() && i < 10; i++)
        {
            int row = i / COLUMNS;
            int col = i % COLUMNS;

            int cardsInRow = Math.min(COLUMNS, cards.size() - (row * COLUMNS));
            int rowWidth = (cardsInRow * cardWidth) + ((cardsInRow - 1) * SPACING);

            int rowStartX = (containerWidth - rowWidth) / 2;

            int x = rowStartX + col * (cardWidth + SPACING) + containerX;
            int y = startY + row * (cardHeight + SPACING) + containerY;

            Rectangle cardBound = cardRenderer.renderCard(graphics, cards.get(i), x, y, cardWidth, cardHeight);

            cardBounds.add(cardBound);
        }
    }

    public boolean isClickOnButton(MouseEvent event) {
        if (event.isConsumed()) return true;

        // Check to see if a card was clicked during a pack opening
        if (overlayStateManager.isPackOpeningOpen() && !overlayStateManager.getOverlayItems().isEmpty()) {
            Rectangle cardBound = isClickOnCard(event);
            if (cardBound != null) {
                cardManager.selectCard(getCardFromBounds(cardBound));
                // Navigate back to the pack overlay
                this.packManager.openAllPacksOverlay();
                return true;
            }
        }

        // Check to see if a card was clicked during the all or held cards view to display the "details" (single card) view
        if (overlayStateManager.isHeldCardsOpen()) {
            Rectangle cardBound = isClickOnCard(event);
            if (cardBound != null) {
                Card card = getCardFromBounds(cardBound);
                this.cardManager.openSingleCardOverlay(card);
                return true;
            }
        }

        // Check to see if a card was clicked during the "Available Cards" view
        if (overlayStateManager.isAvailableCardsOpen()) {
            Rectangle cardBound = isClickOnCard(event);
            if (cardBound != null) {
                Card card = getCardFromBounds(cardBound);
                cardManager.selectCard(card);
                return true;
            }
        }

        return false;
    }

    public Card getCardFromBounds(Rectangle cardRectangle) {
        int index = cardBounds.indexOf(cardRectangle);
        return overlayStateManager.<Card>getOverlayItems().get(index);
    }

    public Rectangle isClickOnCard(MouseEvent event) {
        for (Rectangle cardBound : cardBounds) {
            if (cardBound.contains(event.getPoint())) {
                return cardBound;
            }
        }
        return null;
    }
}