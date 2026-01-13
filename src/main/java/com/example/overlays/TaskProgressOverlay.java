package com.example.overlays;

import com.example.RogueScapeConfig;
import com.example.RogueScapePlugin;
import com.example.tasks.Task;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class TaskProgressOverlay extends Overlay {
    private final RogueScapePlugin plugin;
    private final RogueScapeConfig config;

    private final PanelComponent panel = new PanelComponent();

    @Inject
    public TaskProgressOverlay(RogueScapePlugin plugin, RogueScapeConfig config) {
        this.plugin = plugin;
        this.config = config;

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

        return panel.render(graphics);
    }

    private void buildTaskPanel(Task task)
    {
//        PanelComponent taskPanel = new PanelComponent();
//        taskPanel.setBackgroundColor(config.pinnedTaskBackgroundColor());
//        taskPanel.setBorder(new Rectangle(8, 8, 8, 8)); // padding
//        taskPanel.setBorder(new Rectangle(1, 1));
//        taskPanel.setGap(new Point(0, 4));

        panel.getChildren().add(
                LineComponent.builder()
                        .left(task.getName())
                        .leftColor(config.pinnedTaskTextColor())
                        .build()
        );
//
//        panel.getChildren().add(
//                LineComponent.builder()
//                        .left(task.getDescription())
//                        .build()
//        );
//
//        String progress = task.isComplete()
//                ? "Status: Complete"
//                : "Status: " + task.getCurrent() + "/" + task.getTarget();
//
//        panel.getChildren().add(
//                LineComponent.builder()
//                        .left(progress)
//                        .build()
//        );
    }
}
