package managers;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

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
        // Создаем и добавляем эпик с подзадачей
        int epicId = manager.createEpic(epic);
        manager.createSubtask(subtask);

        // Получаем обновленный эпик
        Epic savedEpic = manager.getEpic(epicId);

        // Проверяем что время установлено правильно
        assertNotNull(savedEpic.getStartTime(), "Время начала эпика должно быть установлено");
        assertEquals(subtask.getStartTime(), savedEpic.getStartTime(),
                "Время начала эпика должно совпадать с подзадачей");
        assertEquals(subtask.getEndTime(), savedEpic.getEndTime(),
                "Время окончания эпика должно совпадать с подзадачей");
    }

    @Test
    void tasksWithoutStartTimeShouldNotBeInPrioritizedList() {
        // Создаем задачу без времени
        Task task = new Task("Task", "Desc", Status.NEW);
        manager.createTask(task);

        // Проверяем что она не попала в prioritizedTasks
        assertTrue(manager.getPrioritizedTasks().isEmpty(),
                "Задачи без времени не должны попадать в prioritizedTasks");

        // Добавляем задачу с временем для проверки
        Task taskWithTime = new Task("TaskWithTime", "Desc", Status.NEW);
        taskWithTime.setStartTime(LocalDateTime.now());
        manager.createTask(taskWithTime);

        // Проверяем что только задача с временем попала в список
        assertEquals(1, manager.getPrioritizedTasks().size(),
                "Только задачи с временем должны быть в prioritizedTasks");
    }

    @Test
    void epicStatusShouldUpdateWhenSubtasksChange() {
        // Создаем эпик с подзадачей
        int epicId = manager.createEpic(epic);
        int subtaskId = manager.createSubtask(subtask);

        // Проверяем начальный статус
        assertEquals(Status.NEW, manager.getEpic(epicId).getStatus(),
                "Начальный статус эпика должен быть NEW");

        // Обновляем подзадачу на DONE
        Subtask updated = new Subtask("Updated", "Desc", Status.DONE, epicId);
        updated.setId(subtaskId);
        updated.setStartTime(subtask.getStartTime());
        updated.setDuration(subtask.getDuration());
        manager.updateSubtask(updated);

        // Проверяем обновленный статус
        assertEquals(Status.DONE, manager.getEpic(epicId).getStatus(),
                "Статус эпика должен измениться на DONE");
    }
}