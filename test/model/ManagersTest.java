package model;

import managers.HistoryManager;
import managers.TaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void getDefaultShouldReturnInitializedTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);

        Task task = new Task("Test", "Desc", Status.NEW);
        task.setStartTime(java.time.LocalDateTime.now());
        task.setDuration(java.time.Duration.ofMinutes(30));
        int id = manager.createTask(task);

        assertNotNull(manager.getTask(id));
        assertNotNull(manager.getPrioritizedTasks());
    }

    @Test
    void getDefaultHistoryShouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);

        Task task = new Task("Test", "Desc", Status.NEW);
        historyManager.add(task);
        assertFalse(historyManager.getHistory().isEmpty());
    }
}