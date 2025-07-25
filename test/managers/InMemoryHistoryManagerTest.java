package managers;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager manager;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        manager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        task2.setId(2);
    }

    @Test
    void addShouldAddTasksToHistory() {
        manager.add(task1);
        manager.add(task2);
        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void addShouldReplaceDuplicateTasks() {
        manager.add(task1);
        Task updatedTask = new Task("Updated", "New desc", Status.DONE);
        updatedTask.setId(1);
        manager.add(updatedTask);

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(updatedTask.getName(), history.get(0).getName());
    }

    @Test
    void removeShouldDeleteTaskFromHistory() {
        manager.add(task1);
        manager.add(task2);
        manager.remove(1);
        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void getHistoryShouldReturnEmptyListWhenEmpty() {
        assertTrue(manager.getHistory().isEmpty());
    }
}