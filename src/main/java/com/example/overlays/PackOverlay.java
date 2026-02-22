package com.example.overlays;

import com.example.RogueScapeConfig;
import com.example.RogueScapePlugin;
import com.example.cards.CardManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class PackOverlay extends Overlay {

    private final RogueScapePlugin plugin;
    private final RogueScapeConfig config;
    private final PanelComponent panel = new PanelComponent();

    @Inject
    PackOverlay(RogueScapePlugin plugin, RogueScapeConfig config) {
        this.plugin = plugin;
        this.config = config;
        panel.setPreferredSize(new Dimension(100, 0)); // width only
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        panel.getChildren().clear();

        panel.setBackgroundColor(config.pinnedTaskBackgroundColor());

        panel.getChildren().add(
                TitleComponent.builder()
                        .text("Available Packs")
                        .color(config.pinnedTaskTextColor())
                        .build()
        );

        CardManager cardManager = this.plugin.getCardManager();
        panel.getChildren().add(
                LineComponent.builder()
                        .left(String.valueOf(cardManager.getAvailablePacks()))
                        .build()
        );

        return panel.render(graphics);
    }
}
