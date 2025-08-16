package model;

import managers.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;
    private Task testTask;
    private Epic testEpic;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();

        testTask = new Task("Проверить менеджер",
                "Протестировать функционал менеджера",
                Status.NEW);
        testTask.setId(1);

        testEpic = new Epic("Рефакторинг тестов",
                "Улучшить тестовое покрытие");
        testEpic.setId(2);
    }

    @Test
    void getDefaultHistory_shouldReturnWorkingHistoryManager() {

        historyManager.add(testTask);

        List<Task> history = historyManager.getHistory();

        assertFalse(history.isEmpty(), "История не должна быть пустой");
        assertEquals(1, history.size(), "В истории должна быть одна задача");
        assertEquals(testTask, history.getFirst(), "Неверная задача в истории");
    }

    @Test
    void getDefault_shouldReturnWorkingTaskManager() {

        taskManager.createTask(testTask);
        taskManager.createEpic(testEpic);

        assertFalse(taskManager.getTasks().isEmpty(), "Список задач не должен быть пустым");
        assertFalse(taskManager.getEpics().isEmpty(), "Список эпиков не должен быть пустым");
        assertEquals(1, taskManager.getTasks().size(), "Неверное количество задач");
        assertEquals(1, taskManager.getEpics().size(), "Неверное количество эпиков");
    }

    @Test
    void historyManager_shouldNotContainDuplicates() {

        Task duplicateTask = new Task("Дубликат", "Та же задача", Status.NEW);
        duplicateTask.setId(testTask.getId());

        historyManager.add(testTask);
        historyManager.add(duplicateTask);
        historyManager.add(testTask);

        assertEquals(1, historyManager.getHistory().size(),
                "История должна содержать только уникальные задачи");
    }

    @Test
    void taskManager_shouldHandleDifferentTaskTypes() {

        Subtask testSubtask = new Subtask("Подзадача",
                "Тестовая подзадача",
                Status.DONE,
                testEpic.getId());

        taskManager.createTask(testTask);
        taskManager.createEpic(testEpic);
        taskManager.createSubtask(testSubtask);

        assertEquals(1, taskManager.getTasks().size());
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubtasks().size());
    }
}