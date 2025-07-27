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
    private Task task3;

    @BeforeEach
    void setUp() {
        manager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        task2.setId(2);
        task3 = new Task("Task 3", "Description 3", Status.DONE);
        task3.setId(3);
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
        manager.add(task3);

        // Удаление из начала
        manager.remove(1);
        assertEquals(List.of(task2, task3), manager.getHistory());

        // Удаление из середины
        manager.remove(3);
        assertEquals(List.of(task2), manager.getHistory());

        // Удаление из конца
        manager.add(task1);
        manager.remove(2);
        assertEquals(List.of(task1), manager.getHistory());
    }

    @Test
    void getHistoryShouldReturnEmptyListWhenEmpty() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void historyShouldNotContainDuplicates() {
        manager.add(task1);
        manager.add(task1);
        manager.add(task1);
        assertEquals(1, manager.getHistory().size());
    }
}