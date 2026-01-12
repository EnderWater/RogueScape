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
    private boolean pinned;

    @Getter
    @Setter
    private int target;

    @Getter
    private int current;

    Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.pinned = false;
        this.isComplete = false;
    }

    Task(String name, String description, int target) {
        this.name = name;
        this.description = description;
        this.target = target;
        this.current = 0;
    }

    Task(String name, boolean pinned, boolean isComplete, String description) {
        this.name = name;
        this.pinned = pinned;
        this.isComplete = isComplete;
        this.description = description;
        this.current = 0;
        this.target = 0;
    }

    private boolean isTaskComplete() {
        return this.current >= this.target;
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