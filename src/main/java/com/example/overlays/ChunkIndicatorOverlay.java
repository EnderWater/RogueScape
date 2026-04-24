package com.example.overlays;

import com.example.IconManager;
import com.example.RogueScapePlugin;
import com.example.packs.PackManager;
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
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

@Singleton
public class ChunkIndicatorOverlay extends Overlay {
    private final IconManager iconManager;
    private final OverlayStateManager overlayStateManager;
    private final PackManager packManager;
    private final Rectangle bounds = new Rectangle();

    private BufferedImage lastIcon;
    private ImageComponent panel;

    @Inject
    ChunkIndicatorOverlay(IconManager iconManager, OverlayStateManager overlayStateManager, PackManager packManager) {
        this.iconManager = iconManager;
        this.overlayStateManager = overlayStateManager;
        this.packManager = packManager;

        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        BufferedImage icon = iconManager.getIconForRegion();

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

        Dimension size = panel.render(graphics);

        bounds.setBounds(
                getBounds().x,
                getBounds().y,
                size.width,
                size.height
        );

        return size;
    }

    public boolean mouseClicked(MouseEvent event) {
        if (event.isConsumed()) return true;

        if (bounds.contains(event.getPoint()) && !overlayStateManager.isAllPacksOpen()) {
            this.packManager.openAllPacksOverlay();
            return true;
        }
        else if (bounds.contains(event.getPoint()) && overlayStateManager.isAllPacksOpen()) {
            overlayStateManager.closeOverlay();
            return true;
        }

        return false;
    }
}
