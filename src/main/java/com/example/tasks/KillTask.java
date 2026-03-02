package com.example.tasks;

public class KillTask extends Task {
    private final String targetName;

    public KillTask(String taskType, String taskName, String description, int current, int target, boolean isPinned, int packsAwarded, String npcName) {
        super(taskType, taskName, description, current, target, isPinned, packsAwarded);
        targetName = npcName;
    }

    public boolean isCurrentKillTarget(String npcName) {
        return this.targetName.equals(npcName);
    }

}
