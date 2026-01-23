 package com.example.panels;

import com.example.cards.CardManager;
import com.example.tasks.KillTask;
import com.example.tasks.SkillTask;
import com.example.tasks.Task;
import com.example.tasks.TaskManager;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.PluginPanel;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class RogueScapePanel extends PluginPanel implements TaskManager.TaskChangeListener {

    private NavigationButton navButton;
    @Inject
    private TaskManager taskManager;
    @Inject
    private CardManager cardManager;
    @Inject
    private PackManagerPanel packManagerPanel;
    private final CollapsiblePanel pinnedSection;
    private final CollapsiblePanel killSection;
    private final CollapsiblePanel skillSection;
    private final CollapsiblePanel addTaskSection;
    private final CollapsiblePanel packManagerSection;

    @Inject
    public RogueScapePanel(TaskManager taskManager, CardManager cardManager)
    {
        this.taskManager = taskManager;
        this.cardManager = cardManager;

        this.taskManager.addListener(this);

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        this.pinnedSection = createSection("Pinned Tasks", mainPanel);
        this.killSection = createSection("Kill Tasks", mainPanel);
        this.skillSection = createSection("Skill Tasks", mainPanel);
        this.addTaskSection = createSection("Add Tasks", mainPanel);
        this.addTaskSection.getContent().add(new TaskGeneratorPanel(this.taskManager));

        this.packManagerSection = createSection("Manage Packs", mainPanel);
        this.packManagerSection.getContent().add(this.packManagerPanel);

        add(mainPanel, BorderLayout.CENTER);

        refreshUI();
    }

    // ------------------- Rendering -------------------

    public void refreshUI() {
        // Get the pinned task
        List<Task> pinnedTasks = this.taskManager.getPinnedTasks();
        // Create the pinned tasks section
        this.createTaskSection(this.pinnedSection.getContent(), pinnedTasks);

        // Get the list of all tasks
        List<Task> tasks = this.taskManager.getTasks();

        // Kill tasks
        this.createTaskSection(this.killSection.getContent(), tasks.stream()
                .filter(t -> t instanceof KillTask && !t.isPinned())
                .collect(Collectors.toList()));

        // Skill tasks
        this.createTaskSection(this.skillSection.getContent(), tasks.stream()
                .filter(t -> t instanceof SkillTask && !t.isPinned())
                .collect(Collectors.toList()));

        revalidate();
        repaint();
    }

    private void updateTaskList(JPanel container, List<Task> tasks) {
        for (Task task : tasks) {
            container.add(createTaskRow(task));
        }
    }

    private CollapsiblePanel createSection(String title, JPanel mainPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(20));
        CollapsiblePanel section = new CollapsiblePanel(panel, title);
        mainPanel.add(section);
        mainPanel.add(Box.createVerticalStrut(20));
        return section;
    }

    private JPanel createTaskRow(Task task) {

        // Create returned panel
        JPanel taskRow = new JPanel();
        taskRow.setLayout(new BoxLayout(taskRow, BoxLayout.Y_AXIS));

        // Create all the rows for the task
        JPanel taskInfoRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel progressBarRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel taskButtonsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel taskButtonsRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Set the rows' alignment
        taskInfoRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressBarRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        taskButtonsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        taskButtonsRow2.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create the name and taskCount labels
        JLabel nameLabel = new JLabel(task.getName());
        JLabel taskCountLabel = new JLabel(task.getCurrent() + "/" + task.getTarget());
        nameLabel.setPreferredSize(new Dimension(120, 20));

        // Create the progress bar
        JProgressBar progressBar = new JProgressBar(0, task.getTarget());
        progressBar.setValue(task.getCurrent());
        progressBar.setStringPainted(true);

        // Create the pin button to pin a task to the top
        JButton pinButton = new JButton();
        pinButton.setPreferredSize(new Dimension(90, 22));
        if (task.isPinned()) {
            // Change the button text
            pinButton.setText("Unpin");

            pinButton.addActionListener(e -> pinTask(task));
        } else {
            pinButton.setText("Pin");
            pinButton.addActionListener(e -> pinTask(task));
        }

        // Create the reset button to reset a task's progress
        JButton resetButton = getResetButton(task);

        // Create the button to add 1 to the current task
        JButton addButton = new JButton("Add 1");
        addButton.setPreferredSize(new Dimension(90, 22));
        addButton.addActionListener(e -> {
            if (!task.isComplete()) {
                task.addToTask(1);

                if (task.isComplete())
                    taskManager.completeTask(task);
            }
            refreshUI();
        });

        // Create the button to remove 1 from the current task
        JButton removeButton = new JButton("Remove 1");
        removeButton.setPreferredSize(new Dimension(90, 22));
        removeButton.addActionListener(e -> {
            if (!task.isComplete() && task.getCurrent() > 0)
                task.removeFromTask(1);
            refreshUI();
        });

        // Add all the UI components to their corresponding row
        taskInfoRow.add(nameLabel);
        taskInfoRow.add(taskCountLabel);
        taskInfoRow.setToolTipText(task.getDescription());
        progressBarRow.add(progressBar);
        taskButtonsRow.add(pinButton);
        taskButtonsRow.add(addButton);
        taskButtonsRow2.add(resetButton);
        taskButtonsRow2.add(removeButton);

        // Add the rows to the wrapping Task row container
        taskRow.add(taskInfoRow);
        taskRow.add(progressBarRow);
        taskRow.add(taskButtonsRow);
        taskRow.add(taskButtonsRow2);
        taskRow.add(Box.createVerticalStrut(20));

        // Return the full task row
        return taskRow;
    }

    @Nonnull
    private JButton getResetButton(Task task) {
        JButton resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(90, 22));
        resetButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to reset this task?",
                    "Confirm Reset",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                task.resetTask();
                refreshUI();
            }
        });
        return resetButton;
    }

    private void createTaskSection(JPanel container, List<Task> tasks) {
        container.removeAll();
        this.updateTaskList(container, tasks);
    }

    // ------------------- Task operations -------------------

    public void pinTask(Task task) {
        this.taskManager.pinTask(task);
        refreshUI();
    }

    @Override
    public void onTasksChanged() {
        SwingUtilities.invokeLater(this::refreshUI);
    }
}
