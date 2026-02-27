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
    private BufferedImage chunkIcon;
    private final ImageComponent panel;

    ChunkIndicatorOverlay() {
//        panel.setPreferredSize(new Dimension(100, 0));
        chunkIcon = ImageUtil.loadImageResource(RogueScapePlugin.class, "/com/example/icons/Quest.png");
        chunkIcon = ImageUtil.resizeImage(chunkIcon, 32, 32);
        panel = new ImageComponent(chunkIcon);
        panel.setPreferredSize(new Dimension(32,32));

        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        return panel.render(graphics);
    }
}
