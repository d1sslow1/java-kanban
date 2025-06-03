package managers;


import model.Epic;
import model.Managers;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;
    private Epic epic;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Task", "Desc", Status.NEW);
        task.setId(1);
        epic = new Epic("Epic", "Epic Desc");
        epic.setId(2);
    }

    @Test
    void shouldAddTasksToHistory() {
        historyManager.add(task);
        historyManager.add(epic);

        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "History size incorrect");
        assertEquals(task, history.get(0), "Tasks don't match");
    }

    @Test
    void shouldNotExceedMaxSize() {
        for (int i = 0; i < 15; i++) {
            Task t = new Task("Task" + i, "Desc", Status.NEW);
            t.setId(i);
            historyManager.add(t);
        }

        assertEquals(10, historyManager.getHistory().size(),
                "History should be limited to 10 items");
    }
}