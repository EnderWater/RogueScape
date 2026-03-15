package com.example.overlays;

import com.example.RogueScapeConfig;
import com.example.RogueScapePlugin;
import com.example.packs.Pack;
import com.example.packs.PackManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PackCountOverlay extends Overlay
{
    private final RogueScapePlugin plugin;
    private final RogueScapeConfig config;
    private final PackManager packManager;
    private final PanelComponent panel = new PanelComponent();
    private final OverlayStateManager overlayStateManager;
    private final Rectangle bounds = new Rectangle();

    @Inject
    PackCountOverlay(RogueScapePlugin plugin, RogueScapeConfig config, PackManager packManager, OverlayStateManager overlayStateManager) {
        this.plugin = plugin;
        this.config = config;
        this.packManager = packManager;
        this.overlayStateManager = overlayStateManager;

        panel.setPreferredSize(new Dimension(120, 0));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        List<Pack> packs = new ArrayList<>(packManager.getPacks());

        panel.getChildren().clear();

        panel.setBackgroundColor(config.pinnedTaskBackgroundColor());

        panel.getChildren().add(
                TitleComponent.builder()
                        .text("Available Packs")
                        .color(config.pinnedTaskTextColor())
                        .build()
        );

        int shown = 0;

        for (Pack pack : packManager.getPacks())
        {
            if (pack.getAvailable() <= 0)
                continue;

            panel.getChildren().add(
                    LineComponent.builder()
                            .left(pack.getName())
                            .right(String.valueOf(pack.getAvailable()))
                            .build()
            );

            shown++;

            if (shown == 3)
                break;
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


    public boolean mouseClicked(MouseEvent event)
    {
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