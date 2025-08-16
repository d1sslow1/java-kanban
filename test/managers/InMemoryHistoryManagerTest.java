package managers;

import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task testTask;
    private Epic testEpic;
    private Subtask testSubtask;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();


        testTask = new Task("Проверить историю",
                "Проверить сохранение задач в истории",
                Status.NEW);
        testTask.setId(1);

        testEpic = new Epic("Рефакторинг кода",
                "Провести рефакторинг всего проекта");
        testEpic.setId(2);

        testSubtask = new Subtask("Написать тесты",
                "Покрыть код unit-тестами",
                Status.IN_PROGRESS,
                testEpic.getId());
        testSubtask.setId(3);
    }

    @Test
    void add_shouldSaveDifferentTaskTypes() {

        historyManager.add(testTask);
        historyManager.add(testEpic);
        historyManager.add(testSubtask);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "Не все задачи сохранились в истории");
        assertTrue(history.contains(testTask), "Задача не найдена в истории");
        assertTrue(history.contains(testEpic), "Эпик не найден в истории");
        assertTrue(history.contains(testSubtask), "Подзадача не найдена в истории");
    }

    @Test
    void remove_shouldDeleteTaskFromHistory() {
        historyManager.add(testTask);
        historyManager.add(testEpic);


        historyManager.remove(testTask.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "Неверное количество задач после удаления");
        assertFalse(history.contains(testTask), "Задача не была удалена");
        assertTrue(history.contains(testEpic), "Эпик не должен был быть удален");
    }

    @Test
    void getHistory_shouldReturnEmptyListForEmptyHistory() {
        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    void add_shouldNotContainDuplicates() {
        // Добавляем задачу 3 раза
        historyManager.add(testTask);
        historyManager.add(testTask);
        historyManager.add(testTask);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "Дубликаты не должны сохраняться");
    }
}