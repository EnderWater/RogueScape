package com.example.overlays;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
import java.awt.event.MouseEvent;

@Singleton
public class WindowOverlay extends Overlay {
    private final Client client;
    private final OverlayStateManager overlayStateManager;
    private final CardListOverlay cardListOverlay;
    private final AvailablePacksOverlay availablePacksOverlay;
    private final TaskListOverlay taskListOverlay;

    private final Rectangle window = new Rectangle();
    private final Rectangle closeButton = new Rectangle();
    private final Rectangle leftArrow = new Rectangle();
    private final Rectangle rightArrow = new Rectangle();

    private final double WINDOW_WIDTH_PERCENTAGE = 0.75;
    private final double WINDOW_HEIGHT_PERCENTAGE = 0.75;

    @Inject
    public WindowOverlay(Client client, OverlayStateManager overlayStateManager, CardListOverlay cardListOverlay, AvailablePacksOverlay availablePacksOverlay, TaskListOverlay taskListOverlay) {
        this.client = client;
        this.overlayStateManager = overlayStateManager;
        this.cardListOverlay = cardListOverlay;
        this.availablePacksOverlay = availablePacksOverlay;
        this.taskListOverlay = taskListOverlay;

        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        if (!this.overlayStateManager.isWindowOpen()) {
            return null;
        }

        int canvasWidth = client.getCanvasWidth();
        int canvasHeight = client.getCanvasHeight();

        int windowWidth = (int) (canvasWidth * WINDOW_WIDTH_PERCENTAGE);
        int windowHeight = (int) (canvasHeight * WINDOW_HEIGHT_PERCENTAGE);

        int x = (canvasWidth - windowWidth) / 2;
        int y = (canvasHeight - windowHeight) / 2;

        window.setBounds(x, y, windowWidth, windowHeight);

        // Background
        graphics2D.setColor(new Color(25, 25, 25, 240));
        graphics2D.fillRoundRect(x, y, windowWidth, windowHeight, 12, 12);

        // Border
        graphics2D.setColor(Color.GRAY);
        graphics2D.drawRoundRect(x, y, windowWidth, windowHeight, 12, 12);

        drawTitleBar(graphics2D, x, y);
        drawPagination(graphics2D, x, y);

        // Update y coordinate and height because the title bar pushes the available space down 28 pixels
        y += 28;
        windowHeight -= 28; // Update for the title bar
        windowHeight -= 30; // Update for the pagination bar

        switch (overlayStateManager.getOverlayComponent()) {
            case AllTasks:
                this.taskListOverlay.render(graphics2D, x, y, windowWidth, windowHeight);
                break;
            case AllPacks:
                this.availablePacksOverlay.render(graphics2D, x, y, windowWidth, windowHeight);
                break;
            default:
                cardListOverlay.render(graphics2D, x, y, windowWidth, windowHeight);
                break;
        }

        return null;
    }

    private void drawTitleBar(Graphics2D g, int x, int y) {
        int barHeight = 28;

        int canvasWidth = client.getCanvasWidth();
        int windowWidth = (int) (canvasWidth * WINDOW_WIDTH_PERCENTAGE);

        g.setColor(new Color(40, 40, 40, 240));
        g.fillRoundRect(x, y, windowWidth, barHeight, 12, 12);

        g.setColor(Color.WHITE);
        switch (this.overlayStateManager.getOverlayComponent()) {
            case SingleCard:
                g.drawString("Card Details", x + 12, y + 18);
                break;
            case PackOpening:
                g.drawString("Select a pack card", x + 12, y + 18);
                break;
            case AvailableCards:
                g.drawString("Available cards", x + 12, y + 18);
                break;
            case HeldCards:
                g.drawString("Held Cards", x + 12, y + 18);
                break;
            case AllPacks:
                g.drawString("Available packs", x + 12, y + 18);
                break;
            case FilteredCards:
                g.drawString("Remaining pack cards", x + 12, y + 18);
                break;
            case None:
            default:
                g.drawString("RogueScape Window", x + 12, y + 18);
        }

        // Close button
        int size = 16;
        int bx = x + windowWidth - size - 10;
        int by = y + 6;

        closeButton.setBounds(bx, by, size, size);

        g.setColor(Color.RED);
        g.drawRect(bx, by, size, size);
        g.drawString("X", bx + 5, by + 14);
    }

    private void drawPagination(Graphics2D g, int x, int y) {
        int canvasWidth = client.getCanvasWidth();
        int canvasHeight = client.getCanvasHeight();

        int windowWidth = (int) (canvasWidth * WINDOW_WIDTH_PERCENTAGE);
        int windowHeight = (int) (canvasHeight * WINDOW_HEIGHT_PERCENTAGE);

        int bottomY = y + windowHeight - 30;

        int centerX = x + windowWidth / 2;

        int arrowSize = 24;

        // Left arrow
        int lx = centerX - 60;
        leftArrow.setBounds(lx, bottomY, arrowSize, arrowSize);

        g.setColor(Color.WHITE);
        g.drawString("<", lx + 10, bottomY + 19); // adjust text position inside box

        // Draw border around left arrow
        g.setColor(Color.GRAY);
        g.drawRect(lx, bottomY, arrowSize, arrowSize);

        // Page text
        String pageText = overlayStateManager.getCurrentPage() + " / " + overlayStateManager.getTotalPages();

        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(pageText);
        int textX = centerX - textWidth / 2;
        int textY = bottomY + 18; // align with arrows

        g.setColor(Color.WHITE);
        g.drawString(pageText, textX, textY);

        // Draw border around page text
        int padding = 4;
        g.setColor(Color.GRAY);
        g.drawRect(textX - padding, bottomY, textWidth + 2 * padding, arrowSize);

        // Right arrow
        int rx = centerX + 44;
        rightArrow.setBounds(rx, bottomY, arrowSize, arrowSize);

        g.setColor(Color.WHITE);
        g.drawString(">", rx + 10, bottomY + 19);

        // Draw border around right arrow
        g.setColor(Color.GRAY);
        g.drawRect(rx, bottomY, arrowSize, arrowSize);
    }

    public boolean handleClick(MouseEvent event) {
        if (event.isConsumed()) return true;

        Point p = event.getPoint();
        int currentPage = overlayStateManager.getCurrentPage();
        int totalPages = overlayStateManager.getTotalPages();

        if (closeButton.contains(p)) {
            this.overlayStateManager.closeOverlay();
            return true;
        }

        if (leftArrow.contains(p)) {
            if (currentPage > 1)
                overlayStateManager.setCurrentPage(currentPage - 1);

            return true;
        }

        if (rightArrow.contains(p)) {
            if (currentPage < totalPages)
                overlayStateManager.setCurrentPage(currentPage + 1);

            return true;
        }

        if (window.contains(p) && overlayStateManager.isWindowOpen())
            return true;

        return false;
    }
}