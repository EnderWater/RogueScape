package com.example.overlays;

import com.example.RogueScapeConfig;
import com.example.RogueScapePlugin;
import com.example.tasks.Task;
import com.example.tasks.TaskManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class PinnedTaskOverlay extends Overlay {
    private final RogueScapePlugin plugin;
    private final RogueScapeConfig config;
    private final TaskManager taskManager;
    private final OverlayStateManager overlayStateManager;

    private final PanelComponent panel = new PanelComponent();
    private final Rectangle bounds = new Rectangle();

    @Inject
    public PinnedTaskOverlay(RogueScapePlugin plugin, RogueScapeConfig config, TaskManager taskManager, OverlayStateManager overlayStateManager) {
        this.plugin = plugin;
        this.config = config;
        this.taskManager = taskManager;
        this.overlayStateManager = overlayStateManager;

        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);

        panel.setPreferredSize(new Dimension(config.overlayWidth(), 0)); // width only
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<Task> pinnedTasks = plugin.getTaskManager().getPinnedTasks();
        if (pinnedTasks.isEmpty()) {
            return null;
        }

        panel.getChildren().clear();
        panel.setPreferredSize(new Dimension(config.overlayWidth(), 0));
        panel.setBackgroundColor(config.pinnedTaskBackgroundColor());
        panel.setGap(new Point(0, 4));

        panel.getChildren().add(
                TitleComponent.builder()
                        .text("Pinned Tasks:")
                        .color(config.pinnedTaskTextColor())
                        .build()
        );

        for (Task task : pinnedTasks) {
            // Figure out how to add space between these guys...
            buildTaskPanel(task);
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

    private void buildTaskPanel(Task task)
    {
        panel.getChildren().add(
                LineComponent.builder()
                        .left(task.getName())
                        .leftColor(config.pinnedTaskTextColor())
                        .build()
        );
        panel.getChildren().add(
                LineComponent.builder()
                        .left(task.getCurrent() + "/" + task.getTarget())
                        .leftColor(config.pinnedTaskTextColor())
                        .build()
        );
    }

    public boolean mouseClicked(MouseEvent event) {
        if (event.isConsumed()) return true;

        if (bounds.contains(event.getPoint()) && !overlayStateManager.isAllTasksOpen()) {
            taskManager.openTaskOverlay();
            return true;
        }
        else if (bounds.contains(event.getPoint())) {
            overlayStateManager.closeOverlay();
            return true;
        }
        return false;
    }
}
