package com.example.tasks;

public class SkillTask extends Task {
    private final String skillName;

    public SkillTask(String taskType, String taskName, String description, int current, int target, boolean pinned, int packsAwarded, String skillName) {
        super(taskType, taskName, description, current, target, pinned, packsAwarded);
        this.skillName = skillName;
    }
}
