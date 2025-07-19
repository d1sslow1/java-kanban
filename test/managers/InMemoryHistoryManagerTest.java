package managers;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        task1.setId(1);
        task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
        task2.setId(2);
    }

    @Test
    void addShouldAddTaskToHistory() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История содержит неверное количество задач.");
        assertEquals(task1, history.get(0), "Задачи не совпадают.");
    }

    @Test
    void addShouldNotContainDuplicates() {
        historyManager.add(task1);
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История содержит дубликаты.");
    }

    @Test
    void removeShouldDeleteTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Задача не удалена из истории.");
        assertEquals(task2, history.get(0), "Удалена неверная задача.");
    }

    @Test
    void getHistoryShouldReturnEmptyListWhenNoTasks() {
        final List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История не пустая.");
    }

    @Test
    void historyShouldMaintainInsertionOrder() {
        historyManager.add(task1);
        historyManager.add(task2);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История содержит неверное количество задач.");
        assertEquals(task1, history.get(0), "Неверный порядок задач.");
        assertEquals(task2, history.get(1), "Неверный порядок задач.");
    }
}