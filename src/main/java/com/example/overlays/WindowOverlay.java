package com.example.overlays;

import com.example.cards.CardManager;
import com.example.packs.PackManager;
import com.example.tasks.TaskManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.client.input.KeyListener;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class WindowOverlay extends Overlay implements KeyListener {

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
    @Inject
    private ModalOverlay modalOverlay;

    private final Rectangle window = new Rectangle();
    private final Rectangle closeButton = new Rectangle();
    private final Rectangle leftArrow = new Rectangle();
    private final Rectangle rightArrow = new Rectangle();
    private final Rectangle searchBar = new Rectangle();
    private String searchText = "";
    private boolean searchFocused = false;

    private final List<TabButton> tabs = new ArrayList<>();

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
    public Dimension render(Graphics2D g) {
        if (!overlayStateManager.isWindowOpen()) return null;

        int canvasWidth = client.getCanvasWidth();
        int canvasHeight = client.getCanvasHeight();

        int windowWidth = (int) (canvasWidth * WINDOW_WIDTH_PERCENTAGE);
        int windowHeight = (int) (canvasHeight * WINDOW_HEIGHT_PERCENTAGE);

        int x = (canvasWidth - windowWidth) / 2;
        int y = (canvasHeight - windowHeight) / 2;

        window.setBounds(x, y, windowWidth, windowHeight);

        // Background
        g.setColor(new Color(25, 25, 25, 240));
        g.fillRoundRect(x, y, windowWidth, windowHeight, 12, 12);

        // Border
        g.setColor(Color.GRAY);
        g.drawRoundRect(x, y, windowWidth, windowHeight, 12, 12);

        drawTitleBar(g, x, y);

        y += 28;

        int tabsHeight = drawTabs(g, x, y, windowWidth);
        y += tabsHeight;

        // Search bar
        drawSearchBar(g);

        int contentPadding = 8;
        windowHeight -= (28 + tabsHeight);

        drawPagination(g, x, y);
        windowHeight -= 30;

        windowHeight -= contentPadding;

        switch (overlayStateManager.getOverlayComponent()) {
            case AllTasks:
                taskListOverlay.render(g, x, y, windowWidth, windowHeight);
                break;
            case AllPacks:
                availablePacksOverlay.render(g, x, y, windowWidth, windowHeight);
                break;
            default:
                cardListOverlay.render(g, x, y, windowWidth, windowHeight);
                break;
        }

        overlayStateManager.getModalOverlay().render(g);

        return null;
    }

    private void drawTitleBar(Graphics2D g, int x, int y) {
        int barHeight = 28;
        int windowWidth = (int) (client.getCanvasWidth() * WINDOW_WIDTH_PERCENTAGE);

        g.setColor(new Color(40, 40, 40, 240));
        g.fillRoundRect(x, y, windowWidth, barHeight, 12, 12);

        g.setColor(Color.WHITE);
        g.drawString("RogueScape Window", x + 12, y + 18);

        int size = 16;
        int bx = x + windowWidth - size - 10;
        int by = y + 6;

        closeButton.setBounds(bx, by, size, size);

        g.setColor(Color.RED);
        g.drawRect(bx, by, size, size);
        g.drawString("X", bx + 5, by + 14);
    }

    private void drawSearchBar(Graphics2D g) {
        int height = 22;
        int padding = 10;
        int gap = 8; // space between arrow and search bar

        // Right boundary (window edge)
        int rightEdge = window.x + window.width - padding;

        // Left boundary (just to the right of the pagination arrow)
        int leftEdge = rightArrow.x + rightArrow.width + gap;

        int width = rightEdge - leftEdge;

        // Safety clamp (in case layout gets weird)
        if (width < 60) width = 60;

        int sx = leftEdge;
        int sy = window.y + window.height - height - padding;

        searchBar.setBounds(sx, sy, width, height);

        // Background
        g.setColor(new Color(50, 50, 50, 240));
        g.fillRoundRect(sx, sy, width, height, 6, 6);

        // Border
        g.setColor(searchFocused ? Color.WHITE : Color.GRAY);
        g.drawRoundRect(sx, sy, width, height, 6, 6);

        // Text
        g.setColor(Color.WHITE);

        String displayText = searchText.isEmpty() && !searchFocused
                ? "Search..."
                : searchText;

        FontMetrics metrics = g.getFontMetrics();

        int textY = sy + ((height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.drawString(displayText, sx + 6, textY);
    }

    private void drawPagination(Graphics2D g, int x, int y) {
        int windowWidth = window.width;
        int windowHeight = window.height;

        int bottomY = window.y + windowHeight - 30;
        int centerX = x + windowWidth / 2;

        int arrowSize = 24;

        int lx = centerX - 60;
        leftArrow.setBounds(lx, bottomY, arrowSize, arrowSize);

        g.setColor(Color.WHITE);
        g.drawString("<", lx + 10, bottomY + 19);
        g.setColor(Color.GRAY);
        g.drawRect(lx, bottomY, arrowSize, arrowSize);

        String pageText = overlayStateManager.getCurrentPage() + " / " + overlayStateManager.getTotalPages();

        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(pageText);

        g.drawString(pageText, centerX - textWidth / 2, bottomY + 18);

        int rx = centerX + 44;
        rightArrow.setBounds(rx, bottomY, arrowSize, arrowSize);

        g.drawString(">", rx + 10, bottomY + 19);
        g.drawRect(rx, bottomY, arrowSize, arrowSize);
    }

    private int drawTabs(Graphics2D g, int x, int y, int windowWidth) {
        int tabHeight = 24;
        int tabWidth = windowWidth / tabs.size();

        for (int i = 0; i < tabs.size(); i++) {
            TabButton tab = tabs.get(i);

            int tx = x + (i * tabWidth);
            tab.bounds.setBounds(tx, y, tabWidth, tabHeight);

            g.setColor(new Color(40, 40, 40, 240));
            g.fillRect(tx, y, tabWidth, tabHeight);

            g.setColor(Color.GRAY);
            g.drawRect(tx, y, tabWidth, tabHeight);

            g.setColor(Color.WHITE);
            g.drawString(tab.label, tx + 10, y + 16);
        }

        return tabHeight;
    }

    public boolean handleClick(MouseEvent event) {
        if (event.isConsumed()) return true;

        Point p = event.getPoint();

        if (searchBar.contains(p)) {
            searchFocused = true;
            return true;
        } else {
            searchFocused = false;
        }

        if (closeButton.contains(p)) {
            overlayStateManager.closeOverlay();
            return true;
        }

        if (leftArrow.contains(p)) {
            overlayStateManager.setCurrentPage(overlayStateManager.getCurrentPage() - 1);
            return true;
        }

        if (rightArrow.contains(p)) {
            overlayStateManager.setCurrentPage(overlayStateManager.getCurrentPage() + 1);
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

        return window.contains(p);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (searchFocused) {
            char c = e.getKeyChar();

            if (Character.isLetterOrDigit(c) || Character.isSpaceChar(c)) {
                searchText += c;
                searchOverlayItems(searchText);
            }
        }

        if (modalOverlay.isVisible()) {
            modalOverlay.onKeyTyped(e.getKeyChar());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (searchFocused) {
            if (e.isConsumed())
                System.out.println("Consumed!");

            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && !searchText.isEmpty()) {
                searchText = searchText.substring(0, searchText.length() - 1);
                searchOverlayItems(searchText);
            }
        }

        if (modalOverlay.isVisible()) {
            modalOverlay.onKeyPressed(e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public void searchOverlayItems(String searchString) {
        this.searchText = searchString;
        overlayStateManager.searchAndUpdateOverlayItems(searchString);
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