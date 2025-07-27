package managers;

import model.*;
import org.junit.jupiter.api.*;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager;
    private Task task;
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();

        task = new Task("Task", "Description", Status.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));

        epic = new Epic("Epic", "Epic Description");

        subtask1 = new Subtask("Subtask 1", "Desc 1", Status.NEW, epic.getId());
        subtask1.setStartTime(LocalDateTime.now().plusHours(1));
        subtask1.setDuration(Duration.ofMinutes(45));

        subtask2 = new Subtask("Subtask 2", "Desc 2", Status.IN_PROGRESS, epic.getId());
        subtask2.setStartTime(LocalDateTime.now().plusHours(2));
        subtask2.setDuration(Duration.ofMinutes(15));
    }

    @Test
    void epicShouldCalculateTimeFieldsCorrectly() {
        int epicId = manager.createEpic(epic);

        // Обновляем время эпика после добавления подзадач
        manager.getEpic(epicId);

        Epic savedEpic = manager.getEpic(epicId);
        assertNotNull(savedEpic.getStartTime(), "StartTime эпика не должен быть null");
        assertNotNull(savedEpic.getEndTime(), "EndTime эпика не должен быть null");
        assertEquals(Duration.ofMinutes(60), savedEpic.getDuration(),
                "Продолжительность эпика должна быть суммой продолжительностей подзадач");
    }

    @Test
    void tasksWithoutStartTimeShouldNotBeInPrioritizedList() {
        Task noTimeTask = new Task("No time", "Desc", Status.NEW);
        manager.createTask(noTimeTask);

        // Создаем вторую задачу без времени
        Task noTimeTask2 = new Task("No time 2", "Desc 2", Status.NEW);
        manager.createTask(noTimeTask2);

        Set<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(0, prioritized.size(),
                "В prioritizedTasks не должно быть задач без startTime");
    }

    @Test
    void epicStatusShouldUpdateWhenSubtasksChange() {
        int epicId = manager.createEpic(epic);
        int subId1 = manager.createSubtask(subtask1);
        int subId2 = manager.createSubtask(subtask2);

        // Изменяем статус обеих подзадач
        Subtask updated1 = new Subtask("Updated 1", "Desc", Status.DONE, epicId);
        updated1.setId(subId1);
        updated1.setStartTime(subtask1.getStartTime());
        updated1.setDuration(subtask1.getDuration());
        manager.updateSubtask(updated1);

        Subtask updated2 = new Subtask("Updated 2", "Desc", Status.IN_PROGRESS, epicId);
        updated2.setId(subId2);
        updated2.setStartTime(subtask2.getStartTime());
        updated2.setDuration(subtask2.getDuration());
        manager.updateSubtask(updated2);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicId).getStatus(),
                "Статус эпика должен быть IN_PROGRESS при разных статусах подзадач");
    }

    @Test
    void getPrioritizedTasksShouldReturnSortedTasks() {
        // Создаем задачи с разным временем
        Task earlyTask = new Task("Early", "Desc", Status.NEW);
        earlyTask.setStartTime(LocalDateTime.now().minusHours(1));
        earlyTask.setDuration(Duration.ofMinutes(30));
        manager.createTask(earlyTask);

        manager.createTask(task);
        manager.createSubtask(subtask1);

        Set<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(3, prioritized.size(),
                "В prioritizedTasks должны быть все задачи с startTime");

        // Проверяем порядок сортировки
        Task first = prioritized.iterator().next();
        assertEquals(earlyTask.getName(), first.getName(),
                "Первой должна быть задача с самым ранним startTime");
    }

    // Остальные тесты остаются без изменений
    @Test
    void createAndGetTaskShouldWorkCorrectly() {
        int taskId = manager.createTask(task);
        Task savedTask = manager.getTask(taskId);
        assertNotNull(savedTask);
        assertEquals(task.getName(), savedTask.getName());
    }

    @Test
    void updateTaskShouldChangeFields() {
        int id = manager.createTask(task);
        Task updated = new Task("New", "New desc", Status.DONE);
        updated.setId(id);
        updated.setStartTime(task.getStartTime());
        updated.setDuration(task.getDuration());
        manager.updateTask(updated);
        assertEquals("New", manager.getTask(id).getName());
    }

    @Test
    void deleteTaskShouldRemoveFromManagerAndHistory() {
        int id = manager.createTask(task);
        manager.getTask(id);
        manager.deleteTask(id);
        assertNull(manager.getTask(id));
        assertTrue(manager.getHistory().isEmpty());
    }
}