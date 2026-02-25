package com.example.overlays;

import com.example.cards.*;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This overlay is meant to show the packs opening for a user. 3-5 random cards will appear based on rarity.
 * The calculations for what cards appear will not appear in this file, instead it will probably be in Pack or CardManager.
 *
 */
public class PackOpeningOverlay extends Overlay {
    private final int OVERLAY_CARD_WIDTH = 150;
    private final int OVERLAY_CARD_HEIGHT = 250;
    private final int MAX_OVERLAY_CARDS = 3;
    private final int PADDING = 12;
    private final int SPACING = 30;

    private final Client client;
    private final ItemManager itemManager;
    private final CardManager cardManager;
    private final Map<String, BufferedImage> iconMap = new HashMap<>();
    private final List<Rectangle> cardBounds = new ArrayList<>();

    @Inject
    PackOpeningOverlay(ItemManager itemManager, CardManager cardManager, Client client) {
        this.itemManager = itemManager;
        this.cardManager = cardManager;
        this.client = client;
        loadIcons();

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);

        setResizable(false);
        setMovable(false);

        int totalWidth = MAX_OVERLAY_CARDS * OVERLAY_CARD_WIDTH + (MAX_OVERLAY_CARDS - 1) * PADDING;
        int startX = (client.getCanvasWidth() - totalWidth) / 2;
        int startY = (client.getCanvasHeight() - OVERLAY_CARD_HEIGHT) / 2;

        for (int i = 0; i < MAX_OVERLAY_CARDS; i++) {
            int x = startX + i * (OVERLAY_CARD_WIDTH + PADDING);
            int y = startY;
            cardBounds.add(new Rectangle(x, y, OVERLAY_CARD_WIDTH, OVERLAY_CARD_HEIGHT));
        }
    }

    private void loadIcons()
    {
        // Card type icons
        loadIcon("Boon", "/com/example/icons/Boon.png");
        loadIcon("Goal", "/com/example/icons/Goal.png");
        loadIcon("Item", "/com/example/icons/Item.png");
        loadIcon("Land", "/com/example/icons/Land.png");
        loadIcon("Main_hand", "/com/example/icons/Main_Hand.png");
        loadIcon("Minigame", "/com/example/icons/Minigame.png");
        loadIcon("Off_hand", "/com/example/icons/Off_Hand.png");
        loadIcon("Quest", "/com/example/icons/Quest.png");
        loadIcon("Relic", "/com/example/icons/Relic.png");
        loadIcon("Skill", "/com/example/icons/Skill.png");

        // Rarity icons
        loadIcon("Common", "/com/example/icons/Common.png");
        loadIcon("Uncommon", "/com/example/icons/Uncommon.png");
        loadIcon("Rare", "/com/example/icons/Rare.png");
        loadIcon("Mythic", "/com/example/icons/Mythic.png");
        loadIcon("Legendary", "/com/example/icons/Legendary.png");
    }

    private void loadIcon(String key, String path)
    {
        try
        {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(path));
            iconMap.put(key, image);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Returns the index of the card that was clicked on screen
     * @param event
     * @return
     */
    public int isClickOnButton(MouseEvent event) {
        if (!this.cardManager.getOverlayCards().isEmpty()) {
            for (Rectangle card : this.cardBounds) {
                if (card.contains(event.getPoint())) {
                    return this.cardBounds.indexOf(card);
                }
            }
        }
        return -1;
    }

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

        List<Card> cards = cardManager.getOverlayCards();

        int cardWidth = OVERLAY_CARD_WIDTH;
        int cardHeight = OVERLAY_CARD_HEIGHT;

        int totalWidth = cards.size() * cardWidth + (cards.size() - 1) * SPACING;
        int startX = (canvasWidth - totalWidth) / 2;
        int startY = (canvasHeight - cardHeight) / 2;

        for (int i = 0; i < cards.size(); i++)
        {
            Card card = cards.get(i);

            int x = startX + i * (cardWidth + SPACING);
            int y = startY;

            int cursorY = y + PADDING;

            // -----------------------------
            // CARD BACKGROUND
            // -----------------------------
            graphics.setColor(new Color(20, 20, 20, 240));
            graphics.fillRoundRect(x, y, cardWidth, cardHeight, 20, 20);

            // Set the border color based on the rarity
            Color borderColor;
            switch (card.getRarity()) {
                case Common:
                    borderColor = new Color(150, 75, 0);
                    break;
                case Uncommon:
                    borderColor = new Color	(64, 145, 18);
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
            // BOTTOM ICONS
            // -----------------------------
            int iconSize = 22;
            int iconY = y + cardHeight - iconSize - PADDING;

            // Rarity icon (BOTTOM LEFT)
            BufferedImage rarityImage = this.iconMap.get(card.getRarity().toString());
            if (rarityImage != null)
            {
                int rarityX = x + PADDING;
                graphics.drawImage(rarityImage, rarityX, iconY, iconSize, iconSize, null);
            }
            else
            {
                // fallback placeholder
                graphics.setColor(Color.YELLOW);
                graphics.fillOval(x + PADDING, iconY, iconSize, iconSize);
            }

            // Type icon (BOTTOM RIGHT)
            BufferedImage typeImage = this.iconMap.get(card.getType());
            if (typeImage != null)
            {
                typeX = x + cardWidth - PADDING - iconSize;
                graphics.drawImage(typeImage, typeX, iconY, iconSize, iconSize, null);
            }
            else
            {
                // fallback placeholder
                graphics.setColor(Color.CYAN);
                graphics.fillOval(x + cardWidth - PADDING - iconSize, iconY, iconSize, iconSize);
            }
        }

        return null;
    }
}
