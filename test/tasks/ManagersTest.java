package tasks;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void getDefaultShouldReturnInitializedTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);

        Task task = new Task("Test", "Desc", Status.NEW);
        int id = manager.createTask(task);
        assertNotNull(manager.getTask(id));
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