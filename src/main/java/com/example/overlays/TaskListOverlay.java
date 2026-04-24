package com.example.overlays;

import com.example.listeners.TaskChangeListener;
import com.example.packs.Pack;
import com.example.tasks.Task;
import com.example.tasks.TaskManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class TaskListOverlay implements TaskChangeListener
{
    private final TaskManager taskManager;
    private final OverlayStateManager overlayStateManager;

    private static final int COLUMNS = 3;
    private static final int ROWS = 2;
    private static final int SPACING = 20;

    private static final int BUTTON_HEIGHT = 22;
    private static final int BUTTON_SPACING = 8;

    private final List<TaskButtonBounds> buttonBounds = new ArrayList<>();

    @Inject
    TaskListOverlay(TaskManager taskManager, OverlayStateManager overlayStateManager) {
        this.taskManager = taskManager;
        this.overlayStateManager = overlayStateManager;

        this.taskManager.addListener(this);
    }

    public void render(Graphics2D g, int containerX, int containerY, int containerWidth, int containerHeight)
    {
        buttonBounds.clear();

        List<Task> tasks = overlayStateManager.getPaginatedItems()
                .stream()
                .map(task -> (Task)task)
                .collect(Collectors.toList());

        int cardWidth = (containerWidth - (SPACING * (COLUMNS - 1))) / COLUMNS;
        int cardHeight = (containerHeight - (SPACING * (ROWS - 1))) / ROWS;

        for (int i = 0; i < tasks.size() && i < 6; i++)
        {
            int row = i / COLUMNS;
            int col = i % COLUMNS;

            int x = containerX + col * (cardWidth + SPACING);
            int y = containerY + row * (cardHeight + SPACING);

            drawTaskCard(g, tasks.get(i), x, y, cardWidth, cardHeight);
        }
    }

    private void drawTaskCard(Graphics2D g, Task task, int x, int y, int width, int height)
    {
        Rectangle bounds = new Rectangle(x, y, width, height);

        drawBackground(g, bounds);
        drawBorder(g, bounds);

        int contentX = x + 10;
        int contentY = y + 22;

        drawTitle(g, task, contentX, contentY);

        contentY += 24;
        drawDescription(g, task, contentX, contentY);

        contentY += 24;
        drawCompletion(g, task, contentX, contentY);

        contentY += 24;
        drawReward(g, task, contentX, contentY);

        drawButtons(g, task, bounds);
    }

    private void drawBackground(Graphics2D g, Rectangle bounds)
    {
        g.setColor(new Color(35,35,35));
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private void drawBorder(Graphics2D g, Rectangle bounds)
    {
        g.setColor(new Color(160,160,160));
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private void drawTitle(Graphics2D g, Task task, int x, int y)
    {
        Font original = g.getFont();
        g.setFont(original.deriveFont(Font.BOLD, 16f));

        g.setColor(Color.WHITE);
        g.drawString(task.getName(), x, y);

        g.setFont(original);
    }

    private void drawDescription(Graphics2D g, Task task, int x, int y)
    {
        g.setColor(Color.LIGHT_GRAY);
        g.drawString(task.getDescription(), x, y);
    }

    private void drawCompletion(Graphics2D g, Task task, int x, int y)
    {
        g.setColor(Color.WHITE);

        String text = task.getCurrent() + "/" + task.getTarget() + " Completed";
        g.drawString(text, x, y);
    }

    private void drawReward(Graphics2D g, Task task, int x, int y)
    {
        FontMetrics metrics = g.getFontMetrics();

        g.setColor(Color.YELLOW);
        g.drawString("Reward:", x, y);

        int rewardLabelWidth = metrics.stringWidth("Reward:");

        g.setColor(Color.WHITE);
        g.drawString(" " + task.getPacksAwarded() + " Booster Packs", x + rewardLabelWidth, y);
    }

    private void drawButtons(Graphics2D g, Task task, Rectangle bounds)
    {
        int buttonWidth = (bounds.width - 40) / 3;

        int y = bounds.y + bounds.height - 32;

        int startX = bounds.x + 10;

        Rectangle complete = drawButton(g, "Complete", startX, y, buttonWidth);
        Rectangle pin = drawButton(g, "Pin", startX + buttonWidth + BUTTON_SPACING, y, buttonWidth);
        Rectangle delete = drawButton(g, "Delete", startX + (buttonWidth + BUTTON_SPACING) * 2, y, buttonWidth);

        buttonBounds.add(new TaskButtonBounds(task, complete, pin, delete));
    }

    private Rectangle drawButton(Graphics2D g, String text, int x, int y, int width)
    {
        Rectangle rect = new Rectangle(x, y, width, BUTTON_HEIGHT);

        g.setColor(new Color(60,60,60));
        g.fillRect(rect.x, rect.y, rect.width, rect.height);

        g.setColor(new Color(150,150,150));
        g.drawRect(rect.x, rect.y, rect.width, rect.height);

        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);

        int textX = rect.x + (rect.width - textWidth) / 2;
        int textY = rect.y + 15;

        g.setColor(Color.WHITE);
        g.drawString(text, textX, textY);

        return rect;
    }

    public TaskButtonBounds getClickedButton(Point point)
    {
        for (TaskButtonBounds bounds : buttonBounds)
        {
            if (bounds.complete.contains(point))
                return bounds.withAction(TaskButtonAction.COMPLETE);

            if (bounds.pin.contains(point))
                return bounds.withAction(TaskButtonAction.PIN);

            if (bounds.delete.contains(point))
                return bounds.withAction(TaskButtonAction.DELETE);
        }

        return null;
    }

    @Override
    public void onTasksChanged() {
        if (this.overlayStateManager.isAllTasksOpen())
            this.overlayStateManager.addOverlayItems(this.taskManager.getTasks());
    }

    public static class TaskButtonBounds
    {
        public final Task task;
        public final Rectangle complete;
        public final Rectangle pin;
        public final Rectangle delete;
        public TaskButtonAction action;

        public TaskButtonBounds(Task task, Rectangle complete, Rectangle pin, Rectangle delete)
        {
            this.task = task;
            this.complete = complete;
            this.pin = pin;
            this.delete = delete;
        }

        public TaskButtonBounds withAction(TaskButtonAction action)
        {
            this.action = action;
            return this;
        }
    }

    public enum TaskButtonAction
    {
        COMPLETE,
        PIN,
        DELETE
    }

    public boolean isClickOnButton(MouseEvent event) {
        if (event.isConsumed()) return true;

        TaskButtonBounds taskButtonBounds = this.getClickedButton(event.getPoint());

        // If the allTasks overlay isn't open, don't do anything with the click
        if (!overlayStateManager.isAllTasksOpen() || taskButtonBounds == null)
            return false;

        switch (taskButtonBounds.action) {
            case PIN:
                taskManager.pinTask(taskButtonBounds.task);
                break;
            case DELETE:
                taskManager.deleteTask(taskButtonBounds.task);
                break;
            case COMPLETE:
                taskManager.completeTask(taskButtonBounds.task);
                break;
        }

        return true;
    }
}
