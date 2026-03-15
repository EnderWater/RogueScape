package com.example.tasks;

import com.example.cards.CardManager;
import com.example.JsonManager;
import com.example.overlays.OverlayStateManager;
import com.example.packs.PackManager;
import com.google.common.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import com.example.listeners.TaskChangeListener;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
@Singleton
public class TaskManager {
    private final JsonManager jsonManager;
    private final PackManager packManager;
    private final OverlayStateManager overlayStateManager;
    private final int MAX_PINNED_TASKS = 3;
    private final int MAX_PACKS_PER_PAGE = 6;
    private final List<TaskChangeListener> listeners = new ArrayList<>();

    @Getter
    private List<Task> tasks = new ArrayList<>();

    @Getter
    @Setter
    private List<Task> pinnedTasks = new ArrayList<>();

    @Inject
    public TaskManager(JsonManager jsonManager, PackManager packManager, OverlayStateManager overlayStateManager) {
        this.jsonManager = jsonManager;
        this.packManager = packManager;
        this.overlayStateManager = overlayStateManager;

        // Load the user's tasks
        List<Task> tasks = jsonManager.load("tasks.json", new TypeToken<List<Task>>(){}.getType());

        // Make sure the tasks loaded are not null. If not, it is safe to set them.
        if (tasks != null)
            this.tasks = tasks;

        // Load any saved pinned tasks
        loadPinnedTasks();
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

    public void saveTasks() {
        jsonManager.save("tasks.json", tasks);
    }

    public void addTask(Task task) {
        this.tasks.add(task);
        this.saveTasks();
        this.notifyListeners();
    }

    public void deleteTask(Task task) {
        this.tasks.remove(task);
        this.pinnedTasks.remove(task);
        this.saveTasks();
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

        this.saveTasks();
        this.notifyListeners();
    }

    public void completeTask(Task task) {
        task.setCompleted();
        this.packManager.addPacks(task.getPacksAwarded(), this.packManager.getCurrentPackName());
        this.notifyListeners();
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

    public void resetTask(Task task) {
        task.resetTask();
        this.notifyListeners();
    }

    public void addToTask(Task task, int amount) {
        if (!task.isTaskComplete()) {
            task.addToTask(amount);

            if (task.isTaskComplete())
                completeTask(task);
        }
        this.notifyListeners();
    }

    public void removeFromTask(Task task, int amount) {
        if (!task.isTaskComplete() && task.getCurrent() > 0)
            task.removeFromTask(amount);
        this.notifyListeners();
    }

    public void loadTasksFromCsv(Path taskFile) {
        List<Task> newTasks = TaskCsvLoader.read(taskFile);
        if (!newTasks.isEmpty()) {
            this.tasks = newTasks;
            this.pinnedTasks.clear();
            this.saveTasks();
            this.notifyListeners();
        }
    }

    public void openTaskOverlay() {
        this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.AllTasks, tasks, MAX_PACKS_PER_PAGE);
    }
}
