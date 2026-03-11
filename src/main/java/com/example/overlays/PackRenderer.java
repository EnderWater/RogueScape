package com.example.overlays;

import com.example.IconManager;
import com.example.packs.Pack;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

@Singleton
public class PackRenderer
{
    private final IconManager iconManager;

    @Inject
    public PackRenderer(IconManager iconManager)
    {
        this.iconManager = iconManager;
    }

    public Rectangle renderPack(Graphics2D graphics, Pack pack, int x, int y, int width, int height)
    {
        Rectangle bounds = new Rectangle(x, y, width, height);

        drawBackground(graphics, bounds);
        drawBorder(graphics, bounds);
        drawPackImage(graphics, pack, bounds);
        drawPackName(graphics, pack, bounds);
        drawPackRemaining(graphics, pack, bounds);

        return bounds;
    }

    private void drawBackground(Graphics2D graphics, Rectangle bounds)
    {
        GradientPaint gradient = new GradientPaint(
                bounds.x,
                bounds.y,
                new Color(40, 40, 40),
                bounds.x,
                bounds.y + bounds.height,
                new Color(20, 20, 20)
        );

        graphics.setPaint(gradient);
        graphics.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
    }

    private void drawBorder(Graphics2D graphics, Rectangle bounds)
    {
        graphics.setColor(new Color(120, 120, 120));
        graphics.setStroke(new BasicStroke(2));
        graphics.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
    }

    private void drawPackImage(Graphics2D graphics, Pack pack, Rectangle bounds)
    {
        BufferedImage image = iconManager.getOverlayIcon(pack.getName());

        if (image == null)
        {
            return;
        }

        int padding = 10;

        int maxWidth = bounds.width - (padding * 2);
        int maxHeight = bounds.height - 60;

        double scale = Math.min(
                (double) maxWidth / image.getWidth(),
                (double) maxHeight / image.getHeight()
        );

        int drawWidth = (int) (image.getWidth() * scale);
        int drawHeight = (int) (image.getHeight() * scale);

        int drawX = bounds.x + (bounds.width - drawWidth) / 2;
        int drawY = bounds.y + padding;

        graphics.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
    }

    private void drawPackName(Graphics2D graphics, Pack pack, Rectangle bounds)
    {
        graphics.setColor(Color.WHITE);

        FontMetrics metrics = graphics.getFontMetrics();

        String name = pack.getName();

        int textWidth = metrics.stringWidth(name);

        int textX = bounds.x + (bounds.width - textWidth) / 2;
        int textY = bounds.y + bounds.height - 32;

        graphics.drawString(name, textX, textY);
    }

    private void drawPackRemaining(Graphics2D graphics, Pack pack, Rectangle bounds)
    {
        Color originalColor = graphics.getColor();

        graphics.setColor(Color.LIGHT_GRAY);

        FontMetrics metrics = graphics.getFontMetrics();

        String text = pack.getAvailable() + " remaining";

        int textWidth = metrics.stringWidth(text);

        int textX = bounds.x + (bounds.width - textWidth) / 2;
        int textY = bounds.y + bounds.height - 14;

        graphics.drawString(text, textX, textY);

        graphics.setColor(originalColor);
    }
}