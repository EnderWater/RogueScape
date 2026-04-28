package com.example.tasks;

import com.example.overlays.OverlayItem;
import lombok.Getter;
import lombok.Setter;

public abstract class Task implements OverlayItem {
    @Getter
    private String taskType;

    @Getter
    private final String name;

    @Getter
    private final String description;

    private boolean isComplete;

    @Getter
    @Setter
    private boolean isPinned;

    @Getter
    @Setter
    private boolean isExpanded = false;

    @Getter
    @Setter
    private int target;

    @Getter
    private int current;

    @Getter
    private int packsAwarded = 0;

    Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.isPinned = false;
        this.isComplete = false;
    }

    Task(String taskType, String taskName, String description, int current, int target, boolean isPinned, int packsAwarded) {
        this.name = taskName;
        this.isPinned = isPinned;
        this.description = description;
        this.current = current;
        this.target = target;
        this.isComplete = isTaskComplete();
        this.taskType = taskType;
        this.packsAwarded = packsAwarded;
    }

    public boolean isTaskComplete() {
        return this.current >= this.target && this.target != 0  && isComplete;
    }

    public void setCompleted() {
        this.isComplete = true;
        this.current = this.target;
    }

    public void resetTask() {
        this.isComplete = false;
        this.current = 0;
    }

    public void addToTask(int x) {
        this.current += x;
    }

    public void removeFromTask(int x) {
        this.current -= x;
    }

    @Override
    public String getSearchableString() {
        return getName() +
                " " +
                getDescription() +
                " " +
                getTaskType();
    }
}
