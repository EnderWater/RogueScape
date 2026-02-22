package com.example.overlays;

import com.example.RogueScapeConfig;
import com.example.RogueScapePlugin;
import com.example.cards.*;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * This overlay is meant to show the packs opening for a user. 3-5 random cards will appear based on rarity.
 * The calculations for what cards appear will not appear in this file, instead it will probably be in Pack or CardManager.
 *
 */
public class PackOpeningOverlay extends Overlay {
    private final PanelComponent panel = new PanelComponent();
    private final RogueScapePlugin plugin;
    private final RogueScapeConfig config;
    private final Client client;
    private final ItemManager itemManager;
    private final CardManager cardManager;

    @Inject
    PackOpeningOverlay(RogueScapePlugin plugin, RogueScapeConfig config, ItemManager itemManager, CardManager cardManager, Client client) {
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        this.cardManager = cardManager;
        this.client = client;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);

        setResizable(false);
        setMovable(false);
    }

//    @Override
//    public Dimension render(Graphics2D graphics) {

//        int canvasWidth = client.getCanvasWidth();
//        int canvasHeight = client.getCanvasHeight();
//
//        List<OverlayCard> cards = cardManager.getOverlayCards();
//        if (cards.isEmpty())
//        {
//            return null;
//        }
//
//        int cardWidth = cards.get(0).getWidth();
//        int cardHeight = cards.get(0).getHeight();
//        int spacing = 20;
//
//        int totalWidth = cards.size() * cardWidth + (cards.size() - 1) * spacing;
//
//        // Center horizontally
//        int startX = (canvasWidth - totalWidth) / 2;
//
//        // Center vertically
//        int startY = (canvasHeight - cardHeight) / 2;
//        // Enable smooth text & shapes
//        graphics.setRenderingHint(
//                RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON
//        );
//
//        // ---------------------------------------------------------------
//
//        for (OverlayCard overlayCard : this.cardManager.getOverlayCards()) {
//
//            // Background
//            graphics.setColor(new Color(30, 30, 30, 220));
//            graphics.fillRoundRect(overlayCard.x, overlayCard.y,
//                    overlayCard.getWidth(), overlayCard.getHeight(), 12, 12);
//
//            // Border
//            graphics.setColor(Color.WHITE);
//            graphics.drawRoundRect(overlayCard.x, overlayCard.y,
//                    overlayCard.getWidth(), overlayCard.getHeight(), 12, 12);
//
//            // Icon
//            BufferedImage image = itemManager.getImage(overlayCard.getCard().getImageId()); // Abyssal whip item ID
//
//            if (image != null) {
//                graphics.drawImage(image, overlayCard.getX() + overlayCard.getWidth()/2, overlayCard.y + overlayCard.getHeight()/2, null);
//            }
//
//            // Text
//            graphics.setColor(Color.WHITE);
//            graphics.drawString(overlayCard.getCard().getName(),
//                    overlayCard.getBottomLeft().x + overlayCard.getWidth() / 2,
//                    overlayCard.getHeight() + 20);
//        }
//
//        return null;
//    }
//    @Override
//    public Dimension render(Graphics2D graphics)
//    {
//        if (cardManager.getOverlayCards().isEmpty())
//        {
//            return null;
//        }
//
//        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//
//        int canvasWidth = client.getCanvasWidth();
//        int canvasHeight = client.getCanvasHeight();
//
//        var cards = cardManager.getOverlayCards();
//
//        int cardWidth = cards.get(0).getWidth();
//        int cardHeight = cards.get(0).getHeight();
//        int spacing = 20;
//
//        int totalWidth = cards.size() * cardWidth + (cards.size() - 1) * spacing;
//
//        int startX = (canvasWidth - totalWidth) / 2;
//        int startY = (canvasHeight - cardHeight) / 2;
//
//        for (int i = 0; i < cards.size(); i++)
//        {
//            OverlayCard overlayCard = cards.get(i);
//
//            int x = startX + i * (cardWidth + spacing);
//            int y = startY;
//
//            // Background
//            graphics.setColor(new Color(30, 30, 30, 220));
//            graphics.fillRoundRect(x, y, cardWidth, cardHeight, 16, 16);
//
//            // Border
//            graphics.setStroke(new BasicStroke(2f));
//            graphics.setColor(Color.WHITE);
//            graphics.drawRoundRect(x, y, cardWidth, cardHeight, 16, 16);
//
//            // Draw Image
//            BufferedImage image = itemManager.getImage(overlayCard.getCard().getImageId());
//            if (image != null)
//            {
//                int imageWidth = image.getWidth();
//
//                int imageX = x + (cardWidth - imageWidth) / 2;
//                int imageY = y + 20;
//
//                graphics.drawImage(image, imageX, imageY, null);
//            }
//
//            // Draw Centered + Wrapped Name
//            graphics.setColor(Color.WHITE);
////            graphics.setFont(new Font("Arial", Font.BOLD, 14));
//
//            FontMetrics metrics = graphics.getFontMetrics();
//            int maxTextWidth = cardWidth - 20;
//            int lineHeight = metrics.getHeight();
//
//            String text = overlayCard.getCard().getName();
//            String[] words = text.split(" ");
//            StringBuilder line = new StringBuilder();
//
//            int textStartY = y + cardHeight - 40;
//            int currentY = textStartY;
//
//            for (String word : words)
//            {
//                String testLine = line + word + " ";
//                int testWidth = metrics.stringWidth(testLine);
//
//                if (testWidth > maxTextWidth)
//                {
//                    int centeredX = x + (cardWidth - metrics.stringWidth(line.toString())) / 2;
//                    graphics.drawString(line.toString(), centeredX, currentY);
//                    line = new StringBuilder(word + " ");
//                    currentY += lineHeight;
//                }
//                else
//                {
//                    line.append(word).append(" ");
//                }
//            }
//
//            if (!(line.length() == 0))
//            {
//                int centeredX = x + (cardWidth - metrics.stringWidth(line.toString())) / 2;
//                graphics.drawString(line.toString(), centeredX, currentY);
//            }
//        }
//
//        return null;
//    }
    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (cardManager.getOverlayCards().isEmpty())
        {
            return null;
        }

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int canvasWidth = client.getCanvasWidth();
        int canvasHeight = client.getCanvasHeight();

        List<OverlayCard> cards = cardManager.getOverlayCards();

        int cardWidth = cards.get(0).getWidth();
        int cardHeight = cards.get(0).getHeight();
        int spacing = 30;

        int totalWidth = cards.size() * cardWidth + (cards.size() - 1) * spacing;
        int startX = (canvasWidth - totalWidth) / 2;
        int startY = (canvasHeight - cardHeight) / 2;

        for (int i = 0; i < cards.size(); i++)
        {
            OverlayCard overlayCard = cards.get(i);
            Card card = overlayCard.getCard();

            int x = startX + i * (cardWidth + spacing);
            int y = startY;

            int padding = 12;
            int cursorY = y + padding;

            // -----------------------------
            // CARD BACKGROUND
            // -----------------------------
            graphics.setColor(new Color(20, 20, 20, 240));
            graphics.fillRoundRect(x, y, cardWidth, cardHeight, 20, 20);

            graphics.setColor(Color.WHITE);
            graphics.setStroke(new BasicStroke(2f));
            graphics.drawRoundRect(x, y, cardWidth, cardHeight, 20, 20);

            // -----------------------------
            // TITLE (RuneScape font, bold + slightly larger)
            // -----------------------------
            Font baseFont = graphics.getFont();
            Font titleFont = baseFont.deriveFont(Font.BOLD, baseFont.getSize() + 4f);
            graphics.setFont(titleFont);
            graphics.setColor(Color.WHITE);

            FontMetrics titleMetrics = graphics.getFontMetrics();
            String title = card.getName();

            int titleWidth = titleMetrics.stringWidth(title);
            int titleX = x + (cardWidth - titleWidth) / 2;
            int titleY = cursorY + titleMetrics.getAscent();

            graphics.drawString(title, titleX, titleY);

            cursorY += titleMetrics.getHeight() + 10;

            // -----------------------------
            // IMAGE (Scaled to width - 20)
            // -----------------------------
            BufferedImage image = itemManager.getImage(card.getImageId());

            if (image != null)
            {
                int maxImageWidth = cardWidth - 20;
                int scaledHeight = (image.getHeight() * maxImageWidth) / image.getWidth();

                int imageX = x + 10;
                int imageY = cursorY;

                graphics.drawImage(image, imageX, imageY, maxImageWidth, scaledHeight, null);

                cursorY += scaledHeight + 15;
            }

            // -----------------------------
            // TYPE TEXT (Centered)
            // -----------------------------
            Font typeFont = baseFont.deriveFont(Font.BOLD, baseFont.getSize() + 1f);
            graphics.setFont(typeFont);

            FontMetrics typeMetrics = graphics.getFontMetrics();
            String typeText = card.getType().toUpperCase();

            int typeWidth = typeMetrics.stringWidth(typeText);
            int typeX = x + (cardWidth - typeWidth) / 2;

            graphics.drawString(typeText, typeX, cursorY);
            cursorY += typeMetrics.getHeight() + 10;

            // -----------------------------
            // DESCRIPTION (Wrapped + Centered)
            // -----------------------------
            graphics.setFont(baseFont); // normal RuneScape font
            FontMetrics descMetrics = graphics.getFontMetrics();

            String description = card.getDescription();
            String[] words = description.split(" ");
            StringBuilder line = new StringBuilder();

            int maxTextWidth = cardWidth - 20;
            int lineHeight = descMetrics.getHeight();

            for (String word : words)
            {
                String testLine = line + word + " ";
                if (descMetrics.stringWidth(testLine) > maxTextWidth)
                {
                    int lineWidth = descMetrics.stringWidth(line.toString());
                    int textX = x + (cardWidth - lineWidth) / 2;
                    graphics.drawString(line.toString(), textX, cursorY);

                    line = new StringBuilder(word + " ");
                    cursorY += lineHeight;
                }
                else
                {
                    line.append(word).append(" ");
                }
            }

            if (!(line.length() == 0))
            {
                int lineWidth = descMetrics.stringWidth(line.toString());
                int textX = x + (cardWidth - lineWidth) / 2;
                graphics.drawString(line.toString(), textX, cursorY);
            }

            // -----------------------------
            // BOTTOM ICONS (Placeholders)
            // -----------------------------
            int iconSize = 22;
            int iconY = y + cardHeight - iconSize - padding;

            // Rarity icon (BOTTOM LEFT)
            graphics.setColor(Color.YELLOW);
            graphics.fillOval(x + padding, iconY, iconSize, iconSize);

            // Type icon (BOTTOM RIGHT)
            graphics.setColor(Color.CYAN);
            graphics.fillOval(x + cardWidth - padding - iconSize, iconY, iconSize, iconSize);
        }

        return null;
    }
}
