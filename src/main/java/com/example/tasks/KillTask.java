package com.example.tasks;

public class KillTask extends Task {

    private final String targetName;

    public KillTask(String taskName, String description) {
        super(taskName, description);
        targetName = "";
    }

    public KillTask(String taskName, String description, int target) {
        super(taskName, description, target);
        targetName = "";
    }

    // Implement the constructor and call the task's constructor
    KillTask(String taskName, String killTargetName, boolean pinned,
             boolean isComplete, String description) {
        super(taskName, pinned, isComplete, description);
        this.targetName = killTargetName;
    }


    public boolean isCurrentKillTarget(String npcName) {
        return this.targetName.equals(npcName);
    }

}
