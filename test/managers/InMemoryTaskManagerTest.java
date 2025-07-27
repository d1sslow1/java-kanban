package managers;

import model.*;
import org.junit.jupiter.api.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
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

        subtask2 = new Subtask("Subtask 2", "Desc 2", Status.NEW, epic.getId());
        subtask2.setStartTime(LocalDateTime.now().plusHours(2));
        subtask2.setDuration(Duration.ofMinutes(15));
    }

    @Test
    void epicShouldCalculateTimeFieldsCorrectly() {
        int epicId = manager.createEpic(epic);
        int subId1 = manager.createSubtask(subtask1);
        int subId2 = manager.createSubtask(subtask2);

        // Обновляем время эпика
        manager.updateEpic(epic); // Добавлен вызов обновления

        Epic savedEpic = manager.getEpic(epicId);
        assertNotNull(savedEpic.getStartTime(), "StartTime эпика не должен быть null");
        assertNotNull(savedEpic.getEndTime(), "EndTime эпика не должен быть null");
        assertEquals(Duration.ofMinutes(60), savedEpic.getDuration(),
                "Продолжительность эпика должна быть суммой продолжительностей подзадач");
    }

    @Test
    void tasksWithoutStartTimeShouldNotBeInPrioritizedList() {
        Task noTimeTask1 = new Task("No time 1", "Desc", Status.NEW);
        Task noTimeTask2 = new Task("No time 2", "Desc 2", Status.NEW);

        manager.createTask(noTimeTask1);
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

        // Изменяем статус одной подзадачи
        Subtask updated = new Subtask("Updated", "Desc", Status.DONE, epicId);
        updated.setId(subId1);
        updated.setStartTime(subtask1.getStartTime());
        updated.setDuration(subtask1.getDuration());
        manager.updateSubtask(updated);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicId).getStatus(),
                "Статус эпика должен быть IN_PROGRESS при разных статусах подзадач");
    }

    @Test
    void getPrioritizedTasksShouldReturnSortedTasks() {
        // Создаем 3 задачи с временем
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = new Task("Task 2", "Desc", Status.NEW);
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        task2.setDuration(Duration.ofMinutes(45));

        Task task3 = new Task("Task 3", "Desc", Status.NEW);
        task3.setStartTime(LocalDateTime.now().plusHours(2));
        task3.setDuration(Duration.ofMinutes(15));

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        Set<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(3, prioritized.size(),
                "В prioritizedTasks должны быть все 3 задачи с startTime");

        // Проверяем порядок сортировки
        Task[] tasks = prioritized.toArray(new Task[0]);
        assertTrue(tasks[0].getStartTime().isBefore(tasks[1].getStartTime()));
        assertTrue(tasks[1].getStartTime().isBefore(tasks[2].getStartTime()));
    }

    @Test
    void createAndGetTaskShouldWorkCorrectly() {
        int taskId = manager.createTask(task);
        Task savedTask = manager.getTask(taskId);
        assertNotNull(savedTask);
        assertEquals(task.getName(), savedTask.getName());
        assertEquals(task.getStartTime(), savedTask.getStartTime());
        assertEquals(task.getDuration(), savedTask.getDuration());
    }

    @Test
    void updateTaskShouldChangeFields() {
        int id = manager.createTask(task);
        Task updated = new Task("New", "New desc", Status.DONE);
        updated.setId(id);
        updated.setStartTime(task.getStartTime().plusHours(1));
        updated.setDuration(Duration.ofHours(1));
        manager.updateTask(updated);

        Task saved = manager.getTask(id);
        assertEquals("New", saved.getName());
        assertEquals(Status.DONE, saved.getStatus());
        assertEquals(updated.getStartTime(), saved.getStartTime());
        assertEquals(updated.getDuration(), saved.getDuration());
    }

    @Test
    void deleteTaskShouldRemoveFromManagerAndHistory() {
        int id = manager.createTask(task);
        manager.getTask(id); // Добавляем в историю
        manager.deleteTask(id);
        assertNull(manager.getTask(id));
        assertTrue(manager.getHistory().isEmpty());
    }
}