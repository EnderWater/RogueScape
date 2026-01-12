package com.example.tasks;

public class SkillTask extends Task {
    private final String skillName;

    public SkillTask(String taskName, String description) {
        super(taskName, description);
        this.skillName = "";
    }

    public SkillTask(String taskName, String description, int target) {
        super(taskName, description, target);
        this.skillName = "";
    }

    public SkillTask(String taskName, String description, String skillName) {
        super(taskName, description);
        this.skillName = skillName;
    }

    public SkillTask(String taskName, String skillName,
                     boolean pinned, boolean isComplete, String description) {
        super(taskName, pinned, isComplete, description);
        this.skillName = skillName;
    }
}
