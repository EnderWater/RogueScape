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

    //    @Override
//    public Dimension render(Graphics2D graphics)
//    {
//        List<Task> pinnedTasks = plugin.getTaskManager().getPinnedTasks();
//        if (pinnedTasks.isEmpty())
//        {
//            return null;
//        }
//
//        panel.getChildren().clear();
//
//        panel.setBackgroundColor(config.pinnedTaskBackgroundColor());
//
//        panel.getChildren().add(
//                TitleComponent.builder()
//                        .text("Current Task")
//                        .color(config.pinnedTaskTextColor())
//                        .build()
//        );
//
//        panel.getChildren().add(
//                LineComponent.builder()
//                        .left(pinnedTask.getName())
//                        .build()
//        );
//
//        panel.getChildren().add(
//                LineComponent.builder()
//                        .left(pinnedTask.getDescription())
//                        .build()
//        );
//
//        String progress = pinnedTask.isComplete()
//                ? "Status: Complete"
//                : "Status: " + pinnedTask.getCurrent() + "/" + pinnedTask.getTarget();
//
//        panel.getChildren().add(
//                LineComponent.builder()
//                        .left(progress)
//                        .build()
//        );
//
//        return panel.render(graphics);
//    }
    @Override
    public Dimension render(Graphics2D graphics) {
        List<Task> pinnedTasks = plugin.getTaskManager().getPinnedTasks();
        if (pinnedTasks.isEmpty()) {
            return null;
        }

        panel.getChildren().clear();
        panel.setBackgroundColor(null);

        for (Task task : pinnedTasks) {
            panel.getChildren().add(buildTaskPanel(task));
        }

        return panel.render(graphics);
    }

    private PanelComponent buildTaskPanel(Task task)
    {
        PanelComponent taskPanel = new PanelComponent();
        taskPanel.setBackgroundColor(config.pinnedTaskBackgroundColor());
        taskPanel.setBorder(new Rectangle(1, 1)); // optional
        taskPanel.setPreferredSize(new Dimension(140, 0));
        taskPanel.setGap(new Point(0, 4));

        taskPanel.getChildren().add(
                TitleComponent.builder()
                        .text("Current Task")
                        .color(config.pinnedTaskTextColor())
                        .build()
        );

        taskPanel.getChildren().add(
                LineComponent.builder()
                        .left(task.getName())
                        .leftColor(config.pinnedTaskTextColor())
                        .build()
        );

        taskPanel.getChildren().add(
                LineComponent.builder()
                        .left(task.getDescription())
                        .build()
        );

        String progress = task.isComplete()
                ? "Status: Complete"
                : "Status: " + task.getCurrent() + "/" + task.getTarget();

        taskPanel.getChildren().add(
                LineComponent.builder()
                        .left(progress)
                        .build()
        );

        return taskPanel;
    }

}
