package com.example.overlays;


import com.example.cards.CardManager;
import com.example.packs.PackManager;
import com.example.tasks.TaskManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class WindowOverlay extends Overlay {
    @Inject
    private Client client;
    @Inject
    private OverlayStateManager overlayStateManager;
    @Inject
    private CardManager cardManager;
    @Inject
    private TaskManager taskManager;
    @Inject
    private PackManager packManager;
    @Inject
    private CardListOverlay cardListOverlay;
    @Inject
    private AvailablePacksOverlay availablePacksOverlay;
    @Inject
    private TaskListOverlay taskListOverlay;

    private final Rectangle window = new Rectangle();
    private final Rectangle closeButton = new Rectangle();
    private final Rectangle leftArrow = new Rectangle();
    private final Rectangle rightArrow = new Rectangle();
    private final List<TabButton> tabs = new ArrayList<TabButton>();

    private final double WINDOW_WIDTH_PERCENTAGE = 0.75;
    private final double WINDOW_HEIGHT_PERCENTAGE = 0.85;

    @Inject
    public WindowOverlay() {
        tabs.add(new TabButton("Tasks", OverlayStateManager.OverlayComponent.AllTasks));
        tabs.add(new TabButton("Packs", OverlayStateManager.OverlayComponent.AllPacks));
        tabs.add(new TabButton("Held Cards", OverlayStateManager.OverlayComponent.HeldCards));
        tabs.add(new TabButton("Available Cards", OverlayStateManager.OverlayComponent.AvailableCards));

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

        y += 28;

        int tabsHeight = drawTabs(graphics2D, x, y, windowWidth);
        int contentPadding = 8;

        y += tabsHeight;
        windowHeight -= (28 + tabsHeight);

        drawPagination(graphics2D, x, y);
        windowHeight -= 30;

        windowHeight -= contentPadding;

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
        int windowWidth = window.width;
        int windowHeight = window.height;

        int bottomY = window.y + windowHeight - 30;

        int centerX = x + windowWidth / 2;

        int arrowSize = 24;

        // Left arrow
        int lx = centerX - 60;
        leftArrow.setBounds(lx, bottomY, arrowSize, arrowSize);

        g.setColor(Color.WHITE);
        g.drawString("<", lx + 10, bottomY + 19);

        g.setColor(Color.GRAY);
        g.drawRect(lx, bottomY, arrowSize, arrowSize);

        // Page text
        String pageText = overlayStateManager.getCurrentPage() + " / " + overlayStateManager.getTotalPages();

        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(pageText);
        int textX = centerX - textWidth / 2;
        int textY = bottomY + 18;

        g.setColor(Color.WHITE);
        g.drawString(pageText, textX, textY);

        int padding = 4;
        g.setColor(Color.GRAY);
        g.drawRect(textX - padding, bottomY, textWidth + 2 * padding, arrowSize);

        // Right arrow
        int rx = centerX + 44;
        rightArrow.setBounds(rx, bottomY, arrowSize, arrowSize);

        g.setColor(Color.WHITE);
        g.drawString(">", rx + 10, bottomY + 19);

        g.setColor(Color.GRAY);
        g.drawRect(rx, bottomY, arrowSize, arrowSize);
    }

    private int drawTabs(Graphics2D g, int x, int y, int windowWidth) {
        int tabHeight = 24;
        int tabCount = tabs.size();

        if (tabCount == 0)
            return 0;

        int tabWidth = windowWidth / tabCount;

        for (int i = 0; i < tabCount; i++) {
            TabButton tab = tabs.get(i);

            int tx = x + (i * tabWidth);
            int ty = y;

            tab.bounds.setBounds(tx, ty, tabWidth, tabHeight);

            boolean active = overlayStateManager.getOverlayComponent() == tab.component;

            if (active)
                g.setColor(new Color(60, 60, 60, 240));
            else
                g.setColor(new Color(40, 40, 40, 240));

            g.fillRect(tx, ty, tabWidth, tabHeight);

            g.setColor(Color.GRAY);
            g.drawRect(tx, ty, tabWidth, tabHeight);

            g.setColor(Color.WHITE);

            FontMetrics metrics = g.getFontMetrics();
            int textWidth = metrics.stringWidth(tab.label);

            int textX = tx + (tabWidth - textWidth) / 2;
            int textY = ty + 16;

            g.drawString(tab.label, textX, textY);
        }

        return tabHeight;
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

        for (TabButton tab : tabs) {
            if (tab.isClickInBounds(p)) {
                switch (tab.component) {
                    case AllTasks:
                        taskManager.openTaskOverlay();
                        break;
                    case AllPacks:
                        packManager.openAllPacksOverlay();
                        break;
                    case HeldCards:
                        cardManager.openHeldCardsOverlay();
                        break;
                    case AvailableCards:
                        cardManager.openAvailableCardsOverlay();
                        break;
                }
            }
        }

        if (window.contains(p) && overlayStateManager.isWindowOpen())
            return true;

        return false;
    }

    public static class TabButton {
        String label;
        OverlayStateManager.OverlayComponent component;
        Rectangle bounds = new Rectangle();

        public TabButton(String label, OverlayStateManager.OverlayComponent component) {
            this.label = label;
            this.component = component;
        }

        public boolean isClickInBounds(Point point) {
            return bounds.contains(point);
        }
    }
}