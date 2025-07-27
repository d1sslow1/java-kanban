package managers;

import model.*;
import org.junit.jupiter.api.*;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Epic", "Description");
        subtask = new Subtask("Subtask", "Desc", Status.NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(Duration.ofMinutes(30));
    }

    @Test
    void epicShouldCalculateTimeFieldsCorrectly() {
        int epicId = manager.createEpic(epic);
        manager.createSubtask(subtask);

        // Явно обновляем эпик
        manager.updateEpicStatus(epic);
        manager.updateEpicTimeFields(epic);

        Epic savedEpic = manager.getEpic(epicId);
        assertNotNull(savedEpic.getStartTime(), "StartTime эпика должен быть установлен");
        assertEquals(subtask.getStartTime(), savedEpic.getStartTime());
        assertEquals(subtask.getEndTime(), savedEpic.getEndTime());
    }

    @Test
    void tasksWithoutStartTimeShouldNotBeInPrioritizedList() {
        Task taskWithoutTime = new Task("Task", "Desc", Status.NEW);
        manager.createTask(taskWithoutTime);

        // Добавляем задачу с временем для проверки
        Task taskWithTime = new Task("TaskWithTime", "Desc", Status.NEW);
        taskWithTime.setStartTime(LocalDateTime.now());
        manager.createTask(taskWithTime);

        Set<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(1, prioritized.size(), "Только задачи с временем должны быть в списке");
        assertTrue(prioritized.contains(taskWithTime));
    }

    @Test
    void epicStatusShouldUpdateWhenSubtasksChange() {
        int epicId = manager.createEpic(epic);
        int subtaskId = manager.createSubtask(subtask);

        // Создаем вторую подзадачу
        Subtask subtask2 = new Subtask("Subtask2", "Desc", Status.NEW, epicId);
        subtask2.setStartTime(LocalDateTime.now().plusHours(1));
        manager.createSubtask(subtask2);

        // Обновляем первую подзадачу
        Subtask updated = new Subtask("Updated", "Desc", Status.DONE, epicId);
        updated.setId(subtaskId);
        updated.setStartTime(subtask.getStartTime());
        updated.setDuration(subtask.getDuration());
        manager.updateSubtask(updated);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicId).getStatus());

        // Обновляем вторую подзадачу
        Subtask updated2 = new Subtask("Updated2", "Desc", Status.DONE, epicId);
        updated2.setId(subtask2.getId());
        manager.updateSubtask(updated2);

        assertEquals(Status.DONE, manager.getEpic(epicId).getStatus());
    }
}