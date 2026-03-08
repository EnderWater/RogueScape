package com.example.tasks;

public class QuestTask extends Task {
    private final String questName;

    public QuestTask(String taskType, String taskName, String description, int current, int target, boolean pinned, int packsAwarded, String questName) {
        super(taskType, taskName, description, current, target, pinned, packsAwarded);
        this.questName = questName;
    }
}
