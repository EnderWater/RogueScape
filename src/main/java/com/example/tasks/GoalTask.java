package com.example.tasks;

public class GoalTask extends Task {
    GoalTask(String name, String description) {
        super(name, description);
    }

    GoalTask(String taskType, String taskName, String description, int current, int target, boolean isPinned, int packsAwarded) {
        super(taskType, taskName, description, current, target, isPinned, packsAwarded);
    }
}
