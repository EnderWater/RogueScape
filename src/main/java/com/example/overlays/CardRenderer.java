package com.example.overlays;

import com.example.IconManager;
import com.example.cards.Card;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CardRenderer {

    private final IconManager iconManager;

    private static final int PADDING = 12;
    private static final int IMAGE_PADDING = 10;
    private static final int ICON_SIZE = 22;

    @Inject
    public CardRenderer(IconManager iconManager) {
        this.iconManager = iconManager;
    }

    public Rectangle renderCard(Graphics2D graphics, Card card, int x, int y, int width, int height) {

        Graphics2D g = (Graphics2D) graphics.create();

        int cursorY = y + PADDING;

        drawBackground(g, x, y, width, height);
        drawBorder(g, card, x, y, width, height);

        cursorY = drawTitle(g, card, x, width, cursorY);

        BufferedImage image = iconManager.getOverlayIcon(card);
        int imageHeight = 0;

        if (image != null) {
            int maxWidth = width - (IMAGE_PADDING * 2);
            imageHeight = (image.getHeight() * maxWidth) / image.getWidth();

            g.drawImage(image, x + IMAGE_PADDING, cursorY, maxWidth, imageHeight, null);
            cursorY += imageHeight + 15;
        }

        cursorY = drawType(g, card, x, width, cursorY);

        int availableDescriptionHeight = height - (cursorY - y) - ICON_SIZE - PADDING - 10;

        cursorY = drawDescription(g, card, x, width, cursorY, availableDescriptionHeight);

        drawIcons(g, card, x, y, width, height);

        g.dispose();

        return new Rectangle(x, y, width, height);
    }

    private void drawBackground(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(new Color(20, 20, 20, 240));
        g.fillRoundRect(x, y, width, height, 20, 20);
    }

    private void drawBorder(Graphics2D g, Card card, int x, int y, int width, int height) {

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
            case Special:
                GradientPaint gradient = new GradientPaint(
                        x, y, new Color(0, 191, 255),
                        x, y + height, new Color(253, 1, 85)
                );
                g.setPaint(gradient);
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(x, y, width, height, 20, 20);
                return;
            default:
                borderColor = Color.WHITE;
        }

        g.setColor(borderColor);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, width, height, 20, 20);
    }

    private int drawTitle(Graphics2D g, Card card, int x, int width, int cursorY) {

        Font baseFont = g.getFont();
        Font titleFont = baseFont.deriveFont(Font.BOLD, baseFont.getSize() + 4f);

        g.setFont(titleFont);
        g.setColor(Color.WHITE);

        FontMetrics metrics = g.getFontMetrics();

        String title = card.getName();

        int titleWidth = metrics.stringWidth(title);
        int titleX = x + (width - titleWidth) / 2;
        int titleY = cursorY + metrics.getAscent();

        g.drawString(title, titleX, titleY);

        g.setFont(baseFont);

        return cursorY + metrics.getHeight() + 10;
    }

    private int drawType(Graphics2D g, Card card, int x, int width, int cursorY) {

        Font baseFont = g.getFont();
        Font typeFont = baseFont.deriveFont(Font.BOLD, baseFont.getSize() + 1f);

        g.setFont(typeFont);

        FontMetrics metrics = g.getFontMetrics();

        String type = card.getType().toUpperCase();

        int typeWidth = metrics.stringWidth(type);
        int typeX = x + (width - typeWidth) / 2;

        g.drawString(type, typeX, cursorY);

        g.setFont(baseFont);

        return cursorY + metrics.getHeight() + 10;
    }

    private int drawDescription(Graphics2D g, Card card, int x, int width, int cursorY, int maxHeight) {

        FontMetrics metrics = g.getFontMetrics();

        String[] words = card.getDescription().split(" ");
        StringBuilder line = new StringBuilder();

        int maxTextWidth = width - 20;
        int startY = cursorY;

        for (String word : words) {

            String test = line + word + " ";

            if (metrics.stringWidth(test) > maxTextWidth) {

                if ((cursorY - startY) + metrics.getHeight() > maxHeight)
                    break;

                int lineWidth = metrics.stringWidth(line.toString());
                int textX = x + (width - lineWidth) / 2;

                g.drawString(line.toString(), textX, cursorY);

                line = new StringBuilder(word + " ");
                cursorY += metrics.getHeight();

            } else {
                line.append(word).append(" ");
            }
        }

        if (line.length() > 0 && (cursorY - startY) + metrics.getHeight() <= maxHeight) {

            int lineWidth = metrics.stringWidth(line.toString());
            int textX = x + (width - lineWidth) / 2;

            g.drawString(line.toString(), textX, cursorY);
            cursorY += metrics.getHeight();
        }

        return cursorY + 10;
    }

    private void drawIcons(Graphics2D g, Card card, int x, int y, int width, int height) {

        int iconY = y + height - ICON_SIZE - PADDING;

        BufferedImage rarityIcon = iconManager.getRarityIcon(card);

        if (rarityIcon != null) {
            g.drawImage(rarityIcon, x + PADDING, iconY, ICON_SIZE, ICON_SIZE, null);
        }

        BufferedImage typeIcon = iconManager.getTypeIcon(card);

        if (typeIcon != null) {
            g.drawImage(typeIcon, x + width - PADDING - ICON_SIZE, iconY, ICON_SIZE, ICON_SIZE, null);
        }
    }
}