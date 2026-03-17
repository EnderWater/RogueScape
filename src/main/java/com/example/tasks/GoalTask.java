package com.example.tasks;

public class GoalTask extends Task {
    public GoalTask(String taskType, String taskName, String description, int current, int target, boolean isPinned, int packsAwarded) {
        super(taskType, taskName, description, current, target, isPinned, packsAwarded);
    }
}
