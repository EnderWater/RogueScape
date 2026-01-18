package com.example.tasks;

import com.example.cards.CardManager;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    @Getter
    private List<Task> tasks;

    @Getter
    @Setter
    private List<Task> pinnedTasks = new ArrayList<>();

    private final CardManager cardManager;
    private final int MAX_PINNED_TASKS = 3;
    private final List<TaskChangeListener> listeners = new ArrayList<>();

    public interface TaskChangeListener {
        void onTasksChanged();
    }

    public void addListener(TaskChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TaskChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (TaskChangeListener l : listeners) {
            l.onTasksChanged();
        }
    }

    @Inject
    public TaskManager(CardManager cardManager) {
        // Load the user's tasks
        this.tasks = TaskReaderWriter.loadTasks();

        // Load any saved pinned tasks
        loadPinnedTasks();

        // Set the cardManager instance to update available packs when a task is completed.
        this.cardManager = cardManager;
    }

    public void saveTasks() {
        TaskReaderWriter.writeTasks(tasks);
    }

    public void addTask(Task task) {
        this.tasks.add(task);
        TaskReaderWriter.writeTasks(tasks);
        this.notifyListeners();
    }

    public void pinTask(Task task) {
        // If it is already in the list, remove it
        if (this.pinnedTasks.contains(task)) {
            task.setPinned(false);
            pinnedTasks.remove(task);
        }
        // If it isn't in the list, add it
        else {
            task.setPinned(true);
            pinnedTasks.add(task);
        }

        // After the task has been added/removed, check if the max has been exceeded
        if (this.pinnedTasks.size() > this.MAX_PINNED_TASKS) {
            // Remove the oldest pinned task
            this.pinnedTasks.get(0).setPinned(false);
            this.pinnedTasks.remove(0);
        }

        this.notifyListeners();
    }

    public void completeTask(Task task) {
        task.setCompleted();
        this.cardManager.addAvailablePack();
    }

    public void loadPinnedTasks() {
        for (Task task : this.tasks) {
            if (this.pinnedTasks.size() >= this.MAX_PINNED_TASKS) {
                break;
            }

            if (task.isPinned()) {
                this.pinnedTasks.add(task);
            }
        }
    }
}
