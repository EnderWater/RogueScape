package com.example.overlays;

import com.example.RogueScapePlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

@Singleton
public class ChunkIndicatorOverlay extends Overlay {
    private final OverlayStateManager overlayStateManager;

    private BufferedImage lastIcon;
    private ImageComponent panel;

    @Inject
    ChunkIndicatorOverlay(OverlayStateManager overlayStateManager) {
        this.overlayStateManager = overlayStateManager;

        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        BufferedImage icon = overlayStateManager.getIconForRegion();

        if (icon == null)
        {
            return null;
        }

        if (icon != lastIcon)
        {
            lastIcon = icon;
            panel = new ImageComponent(icon);
            panel.setPreferredSize(new Dimension(32, 32));
        }

        return panel.render(graphics);
    }
}
