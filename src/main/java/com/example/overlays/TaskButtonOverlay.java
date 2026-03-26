package com.example.overlays;

import com.example.RogueScapeConfig;
import com.example.tasks.Task;
import com.example.tasks.TaskManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Moved to using the PinnedTaskOverlay to open up the task list.
 */
@Deprecated
public class TaskButtonOverlay extends Overlay {

    private final RogueScapeConfig config;
    private final PanelComponent panel = new PanelComponent();
    private final OverlayStateManager overlayStateManager;
    private final TaskManager taskManager;
    private final Rectangle bounds = new Rectangle();

    @Inject
    TaskButtonOverlay(RogueScapeConfig config, OverlayStateManager overlayStateManager, TaskManager taskManager) {
        this.config = config;
        this.overlayStateManager = overlayStateManager;
        this.taskManager = taskManager;

        panel.setPreferredSize(new Dimension(120, 0));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panel.getChildren().clear();

        panel.setBackgroundColor(config.pinnedTaskBackgroundColor());

        panel.getChildren().add(
                TitleComponent.builder()
                        .text("Open Tasks")
                        .color(config.pinnedTaskTextColor())
                        .build()
        );

        Dimension size = panel.render(graphics);

        bounds.setBounds(
                getBounds().x,
                getBounds().y,
                size.width,
                size.height
        );

        return size;
    }
}
