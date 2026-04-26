package com.example.panels;

import com.example.tasks.*;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class TaskGeneratorPanel extends JPanel {

    private final TaskManager taskManager;
    private final JComboBox<String> taskType;
    private final JTextField taskName;
    private final JTextField taskDescription;
    private final JSpinner taskTarget;
    private final JSpinner packsAwarded;
//    private final JComboBox<Skill> skill;

    @Inject
    TaskGeneratorPanel(TaskManager taskManager) {
        this.taskManager = taskManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(10));

        String[] taskTypes = new String[] {"Kill", "Skill", "Quest", "Miscellaneous"};

        taskType = createComboBox("Task Type", taskTypes);
        taskName = addTextField("Task name");
        taskDescription = addTextField("Task description");
        taskTarget = addSpinner("# to complete goal");
        packsAwarded = addSpinner("# packs awarded");
        createButtons();
    }

    String getTaskType() {
        return Objects.requireNonNull(taskType.getSelectedItem()).toString();
    }

    String getTaskName() {
        if (taskName.getText().isBlank()) {
            JOptionPane.showMessageDialog(taskName, "Task name cannot be empty");
            return null;
        }
        else
            return taskName.getText();
    }

    String getTaskDescription() {
        if (taskDescription.getText().isBlank()) {
            JOptionPane.showMessageDialog(taskName, "Task name cannot be empty");
            return null;
        }
        else
            return taskDescription.getText();
    }

    int getTaskTarget() {
        return (int)this.taskTarget.getValue();
    }

    int getPacksAwarded() {
        return (int)this.packsAwarded.getValue();
    }

    private JTextField addTextField(String labelString) {
        final JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        final JLabel label = new JLabel(labelString);
        final FlatTextField uiInput = new FlatTextField();

        uiInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        uiInput.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        uiInput.setBorder(new EmptyBorder(5, 7, 5, 7));

        label.setFont(FontManager.getRunescapeSmallFont());
        label.setBorder(new EmptyBorder(0, 0, 4, 0));
        label.setForeground(Color.WHITE);

        container.add(label, BorderLayout.NORTH);
        container.add(uiInput, BorderLayout.CENTER);

        add(container);
        add(Box.createVerticalStrut(20));

        return uiInput.getTextField();
    }

    private <T> JComboBox<T> createComboBox(String label, T[] options) {
        final JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        final JLabel boxLabel = new JLabel(label);
        JComboBox<T> box = new JComboBox<>(options);

        boxLabel.setFont(FontManager.getRunescapeSmallFont());
        boxLabel.setBorder(new EmptyBorder(0, 0, 4, 0));

        container.add(boxLabel, BorderLayout.NORTH);
        container.add(box, BorderLayout.CENTER);

        add(container);
        add(Box.createVerticalStrut(20));

        return box;
    }

    private void createButtons() {
        JButton submit = new JButton("Create task");
        submit.addActionListener(e -> this.submit());

        JButton loadFromCsvButton = new JButton("Load tasks");
        loadFromCsvButton.setPreferredSize(new Dimension(120, 22));
        loadFromCsvButton.addActionListener(e -> this.loadFromCsv(loadFromCsvButton));

        JPanel submitContainer = new JPanel();
        submitContainer.setLayout(new BorderLayout());

        submitContainer.add(submit, BorderLayout.WEST);
        submitContainer.add(loadFromCsvButton, BorderLayout.EAST);
        add(submitContainer);
    }

    private JSpinner addSpinner(String label) {
        final JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        JLabel boxLabel = new JLabel(label);
        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 5096, 1);
        JSpinner spinner = new JSpinner(spinnerModel);

        boxLabel.setFont(FontManager.getRunescapeSmallFont());
        boxLabel.setBorder(new EmptyBorder(0, 0, 4, 0));

        container.add(boxLabel, BorderLayout.NORTH);
        container.add(spinner);

        add(container);
        add(Box.createVerticalStrut(20));

        return spinner;
    }

    private void clearFormData() {
        this.taskDescription.setText("");
        this.taskName.setText("");
//        this.skill.setSelectedIndex(0);
        this.taskType.setSelectedIndex(0);
    }

    private void submit() {
        String taskName = this.getTaskName();
        if (taskName == null) return;

        String taskDesc = this.getTaskDescription();
        if (taskDesc == null) return;

        String taskType = this.getTaskType();
        int target = this.getTaskTarget();
        int packsAwarded = this.getPacksAwarded();

        switch (taskType) {
            case "Kill":
                KillTask killTask = new KillTask(taskType, taskName, taskDesc, 0, target, false, packsAwarded, "");
                taskManager.addTask(killTask);
                break;

            case "Skill":
                SkillTask skillTask = new SkillTask(taskType, taskName, taskDesc, 0, target, false, packsAwarded, "");
                taskManager.addTask(skillTask);
                break;

            case "Quest":
                QuestTask questTask = new QuestTask(taskType, taskName, taskDesc, 0, target, false, packsAwarded, "");
                taskManager.addTask(questTask);
                break;

            case "Miscellaneous":
                MiscellaneousTask miscellaneousTask = new MiscellaneousTask(taskType, taskName, taskDesc, 0, target, false, packsAwarded);
                taskManager.addTask(miscellaneousTask);
                break;
        }
        this.clearFormData();
    }

    private void loadFromCsv(JButton loadFromCsvButton) {
        Path taskFile = Paths.get("assets", "data", "loadFromCsv", "tasks", "tasks.csv");
        // Create the dialog string to show the path of the csv
        String dialog = "Would you like to load tasks from " + taskFile + "?";

        int response = JOptionPane.showConfirmDialog(loadFromCsvButton, dialog);
        if (response != JOptionPane.YES_OPTION) return;

        String[] options = {"Append", "Overwrite", "Cancel"};

        response = JOptionPane.showOptionDialog(
                loadFromCsvButton,
                "How would you like to load the tasks?",
                "Load Type",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (response) {
            case 0: // Append
                this.taskManager.appendTasksFromCsv(taskFile);
                break;
            case 1: // Overwrite
                this.taskManager.overwriteTasksFromCsv(taskFile);
                break;
        }
    }
}
