package managers;

import model.*;
import org.junit.jupiter.api.*;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager;
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

        // Явно обновляем время эпика
        ((InMemoryTaskManager) manager).updateEpicTimeFields(epic);

        Epic savedEpic = manager.getEpic(epicId);
        assertNotNull(savedEpic.getStartTime(), "StartTime эпика должен быть установлен");
        assertEquals(subtask.getStartTime(), savedEpic.getStartTime(), "Время начала должно совпадать с подзадачей");
    }

    @Test
    void tasksWithoutStartTimeShouldNotBeInPrioritizedList() {
        Task task1 = new Task("Task1", "Desc", Status.NEW); // Без времени
        Task task2 = new Task("Task2", "Desc", Status.NEW); // Без времени
        manager.createTask(task1);
        manager.createTask(task2);

        assertTrue(manager.getPrioritizedTasks().isEmpty(),
                "Задачи без времени не должны попадать в prioritizedTasks");
    }

    @Test
    void epicStatusShouldUpdateWhenSubtasksChange() {
        int epicId = manager.createEpic(epic);
        int subtaskId = manager.createSubtask(subtask);

        // Создаем обновленную версию подзадачи с новым статусом
        Subtask updatedSubtask = new Subtask("Updated", "Desc", Status.DONE, epicId);
        updatedSubtask.setId(subtaskId);
        updatedSubtask.setStartTime(subtask.getStartTime());
        updatedSubtask.setDuration(subtask.getDuration());

        manager.updateSubtask(updatedSubtask);

        assertEquals(Status.DONE, manager.getEpic(epicId).getStatus(),
                "Статус эпика должен обновиться после изменения подзадачи");
    }
}