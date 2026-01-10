package com.example;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class TaskProgressOverlay extends Overlay {
    private final RogueScapePlugin plugin;

    @Inject
    public TaskProgressOverlay(RogueScapePlugin plugin) {
        this.plugin = plugin;
        // Set the position of the overlay
        setPosition(OverlayPosition.TOP_LEFT);

        // Set the layer of the overlay (if it is on top of other things)
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    public Dimension render(Graphics2D graphics) {
        graphics.setColor(Color.GREEN);
        graphics.drawRect(0, 0, 100, 100);

        List<String> previousKills = plugin.getPreviousKills();

        int startingX = 20;
        int listPosition = 0;
        for (int i=previousKills.size()-1; i >= 0; i--) {
            graphics.drawString(previousKills.get(i), 5, startingX + (listPosition * 15));
            listPosition++;
        }
        return null;
    }
}
