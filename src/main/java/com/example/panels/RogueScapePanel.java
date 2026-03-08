 package com.example.panels;

import com.example.cards.CardManager;
import com.example.listeners.TaskChangeListener;
import com.example.overlays.OverlayStateManager;
import com.example.tasks.*;
import net.runelite.client.ui.PluginPanel;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class RogueScapePanel extends PluginPanel implements TaskChangeListener {

    private final TaskManager taskManager;
    private final CardManager cardManager;
    private final OverlayStateManager overlayStateManager;

    private PackManagerPanel packManagerPanel;
    private final CollapsiblePanel pinnedSection;
    private final CollapsiblePanel killSection;
    private final CollapsiblePanel skillSection;
    private final CollapsiblePanel questSection;
    private final CollapsiblePanel miscellaneousSection;
    private final CollapsiblePanel completedSection;
    private final CollapsiblePanel addTaskSection;
    private final CollapsiblePanel packManagerSection;

    @Inject
    public RogueScapePanel(TaskManager taskManager, CardManager cardManager, OverlayStateManager overlayStateManager)
    {
        this.taskManager = taskManager;
        this.cardManager = cardManager;
        this.overlayStateManager = overlayStateManager;
        this.packManagerPanel = new PackManagerPanel(this.cardManager, this.overlayStateManager);

        this.taskManager.addListener(this);

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        this.pinnedSection = createSection("Pinned Tasks", mainPanel);
        this.killSection = createSection("Kill Tasks", mainPanel);
        this.skillSection = createSection("Skill Tasks", mainPanel);
        this.questSection = createSection("Quest tasks", mainPanel);
        this.miscellaneousSection = createSection("Miscellaneous tasks", mainPanel);
        this.completedSection = createSection("Completed Tasks", mainPanel);
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
        this.createTaskSection(this.pinnedSection, pinnedTasks);

        // Get the list of all tasks
        List<Task> tasks = this.taskManager.getTasks();

        // Kill tasks
        this.createTaskSection(this.killSection, tasks.stream()
                .filter(t -> t instanceof KillTask && !t.isPinned() && !t.isTaskComplete())
                .collect(Collectors.toList()));

        // Skill tasks
        this.createTaskSection(this.skillSection, tasks.stream()
                .filter(t -> t instanceof SkillTask && !t.isPinned() && !t.isTaskComplete())
                .collect(Collectors.toList()));

        // Skill tasks
        this.createTaskSection(this.questSection, tasks.stream()
                .filter(t -> t instanceof QuestTask && !t.isPinned() && !t.isTaskComplete())
                .collect(Collectors.toList()));

        // Skill tasks
        this.createTaskSection(this.miscellaneousSection, tasks.stream()
                .filter(t -> t instanceof MiscellaneousTask && !t.isPinned() && !t.isTaskComplete())
                .collect(Collectors.toList()));

        // Completed tasks
        this.createTaskSection(this.completedSection, tasks.stream()
                .filter(t -> t.isTaskComplete() && !t.isPinned())
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
        JPanel taskInfoRow = new JPanel(new BorderLayout());
        JPanel progressBarRow = new JPanel(new BorderLayout());
        JPanel taskButtons = new JPanel(new GridLayout(0,2,8,8));

        taskInfoRow.setToolTipText(task.getDescription());

        // Create the name and taskCount labels
        JLabel nameLabel = new JLabel(task.getName());
        nameLabel.setFont(new Font(this.getFont().getName(), this.getFont().getStyle(), 20));
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
            this.taskManager.addToTask(task, 1);
        });

        // Create the button to remove 1 from the current task
        JButton removeButton = new JButton("Remove 1");
        removeButton.setPreferredSize(new Dimension(90, 22));
        removeButton.addActionListener(e -> {
            this.taskManager.removeFromTask(task, 1);
        });

        JButton deleteButton = new JButton("Delete Task");
        deleteButton.setPreferredSize(new Dimension(90, 22));
        deleteButton.addActionListener(e -> {
            this.taskManager.deleteTask(task);
        });

        JButton completeTaskButton = new JButton("Complete Task");
        completeTaskButton.setPreferredSize(new Dimension(90, 22));
        completeTaskButton.addActionListener(e -> {
            this.taskManager.completeTask(task);
        });

        // Add all the UI components to their corresponding row
        taskInfoRow.add(nameLabel, BorderLayout.WEST);
        taskInfoRow.add(taskCountLabel, BorderLayout.EAST);
        progressBarRow.add(progressBar, BorderLayout.CENTER);
        progressBarRow.add(Box.createVerticalStrut(8), BorderLayout.SOUTH);
        taskButtons.add(pinButton);
        taskButtons.add(addButton);
        taskButtons.add(resetButton);
        taskButtons.add(removeButton);
        taskButtons.add(deleteButton);
        taskButtons.add(completeTaskButton);

        // Add the rows to the wrapping Task row container
        taskRow.add(taskInfoRow);
        taskRow.add(progressBarRow);
        taskRow.add(taskButtons);
//        taskRow.add(taskButtonsRow2);
//        taskRow.add(taskButtonsRow3);
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
                taskManager.resetTask(task);
            }
        });
        return resetButton;
    }

    private void createTaskSection(CollapsiblePanel container, List<Task> tasks) {
        JPanel content = container.getContent();
        content.removeAll();

//        if (container.isExpanded())
        this.updateTaskList(content, tasks);
    }

    // ------------------- Task operations -------------------

    public void pinTask(Task task) {
        this.taskManager.pinTask(task);
    }

    @Override
    public void onTasksChanged() {
        SwingUtilities.invokeLater(this::refreshUI);
    }
}
