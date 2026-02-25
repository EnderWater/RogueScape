package com.example.tasks;

import lombok.Getter;
import lombok.Setter;

public abstract class Task {
    private String taskType;

    @Getter
    private final String name;

    @Getter
    private final String description;

    @Getter
    private boolean isComplete;

    @Getter
    @Setter
    private boolean isPinned;

    @Getter
    @Setter
    private int target;

    @Getter
    private int current;

    Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.isPinned = false;
        this.isComplete = false;
    }

    Task(String name, String description, int target) {
        this.name = name;
        this.description = description;
        this.target = target;
        this.current = 0;
    }

    Task(String taskType, String name, String description, int target) {
        this.name = name;
        this.description = description;
        this.target = target;
        this.taskType = taskType;
        this.current = 0;
    }

    Task(String taskType, String taskName, String description, int current, int target, boolean isPinned) {
        this.name = taskName;
        this.isPinned = isPinned;
        this.description = description;
        this.current = current;
        this.target = target;
        this.isComplete = isTaskComplete();
        this.taskType = taskType;
    }

    private boolean isTaskComplete() {
        return this.current >= this.target && this.target != 0;
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

        if (this.isTaskComplete()) {
            setCompleted();
        }
    }

    public void removeFromTask(int x) {
        this.current -= x;
    }
}
