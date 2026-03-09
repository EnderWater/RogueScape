package com.example.overlays;

import com.example.cards.Card;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class CardRenderer {
    private final ItemManager itemManager;
    private final OverlayStateManager overlayStateManager;

    private final int PADDING = 12;

    @Inject
    public CardRenderer(ItemManager itemManager, OverlayStateManager overlayStateManager) {
        this.itemManager = itemManager;
        this.overlayStateManager = overlayStateManager;
    }

    public Rectangle renderCard(Graphics2D graphics, Card card, int x, int y, int width, int height) {
        int cursorY = y + PADDING;

        // -----------------------------
        // BACKGROUND
        // -----------------------------
        graphics.setColor(new Color(20, 20, 20, 240));
        graphics.fillRoundRect(x, y, width, height, 20, 20);

        // -----------------------------
        // BORDER
        // -----------------------------
        Color borderColor;

        switch (card.getRarity()) {
            case Common:
                borderColor = new Color(150, 75, 0);
                break;
            case Uncommon:
                borderColor = new Color(64, 145, 18);
                break;
            case Rare:
                borderColor = new Color(135, 206, 235);
                break;
            case Mythic:
                borderColor = new Color(175, 149, 230);
                break;
            case Legendary:
                borderColor = new Color(250, 224, 51);
                break;
            default:
                borderColor = Color.WHITE;
        }

        graphics.setColor(borderColor);
        graphics.setStroke(new BasicStroke(2f));
        graphics.drawRoundRect(x, y, width, height, 20, 20);

        // -----------------------------
        // TITLE
        // -----------------------------
        Font baseFont = graphics.getFont();
        Font titleFont = baseFont.deriveFont(Font.BOLD, baseFont.getSize() + 4f);

        graphics.setFont(titleFont);
        graphics.setColor(Color.WHITE);

        FontMetrics metrics = graphics.getFontMetrics();

        String title = card.getName();

        int titleWidth = metrics.stringWidth(title);
        int titleX = x + (width - titleWidth) / 2;
        int titleY = cursorY + metrics.getAscent();

        graphics.drawString(title, titleX, titleY);

        cursorY += metrics.getHeight() + 10;

        // -----------------------------
        // IMAGE
        // -----------------------------
        BufferedImage image = itemManager.getImage(card.getImageId());

        if (image != null) {
            int maxWidth = width - 20;
            int scaledHeight = (image.getHeight() * maxWidth) / image.getWidth();

            graphics.drawImage(image, x + 10, cursorY, maxWidth, scaledHeight, null);

            cursorY += scaledHeight + 15;
        }

        // -----------------------------
        // TYPE
        // -----------------------------
        Font typeFont = baseFont.deriveFont(Font.BOLD, baseFont.getSize() + 1f);
        graphics.setFont(typeFont);

        metrics = graphics.getFontMetrics();

        String type = card.getType().toUpperCase();

        int typeWidth = metrics.stringWidth(type);
        int typeX = x + (width - typeWidth) / 2;

        graphics.drawString(type, typeX, cursorY);

        cursorY += metrics.getHeight() + 10;

        // -----------------------------
        // DESCRIPTION
        // -----------------------------
        graphics.setFont(baseFont);
        metrics = graphics.getFontMetrics();

        String[] words = card.getDescription().split(" ");
        StringBuilder line = new StringBuilder();

        int maxTextWidth = width - 20;

        for (String word : words) {
            String test = line + word + " ";

            if (metrics.stringWidth(test) > maxTextWidth) {
                int lineWidth = metrics.stringWidth(line.toString());
                int textX = x + (width - lineWidth) / 2;

                graphics.drawString(line.toString(), textX, cursorY);

                line = new StringBuilder(word + " ");
                cursorY += metrics.getHeight();
            } else {
                line.append(word).append(" ");
            }
        }

        if (line.length() > 0) {
            int lineWidth = metrics.stringWidth(line.toString());
            int textX = x + (width - lineWidth) / 2;

            graphics.drawString(line.toString(), textX, cursorY);
        }

        // -----------------------------
        // ICONS
        // -----------------------------
        int iconSize = 22;
        int iconY = y + height - iconSize - PADDING;

        BufferedImage rarityIcon = overlayStateManager.getOverlayIcon(card.getRarity().toString());

        if (rarityIcon != null) {
            graphics.drawImage(rarityIcon, x + PADDING, iconY, iconSize, iconSize, null);
        }

        BufferedImage typeIcon = overlayStateManager.getOverlayIcon(card.getType());

        if (typeIcon != null) {
            graphics.drawImage(typeIcon, x + width - PADDING - iconSize, iconY, iconSize, iconSize, null);
        }

        // Return the bounds of the card that was rendered
        return new Rectangle(x, y, width, height);
    }
}