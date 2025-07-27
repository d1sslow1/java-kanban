package managers;

import model.*;
import org.junit.jupiter.api.*;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Epic", "Description");
    }

    @Test
    void epicShouldCalculateTimeFieldsCorrectly() {

        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(Duration.ofMinutes(30));

        int epicId = manager.createEpic(epic);
        manager.createSubtask(subtask);

        Epic savedEpic = manager.getEpic(epicId);
        assertNotNull(savedEpic.getStartTime(), "Время начала эпика должно быть установлено");
        assertEquals(subtask.getStartTime(), savedEpic.getStartTime());
        assertEquals(subtask.getEndTime(), savedEpic.getEndTime());
    }

    @Test
    void tasksWithoutStartTimeShouldNotBeInPrioritizedList() {

        Task taskWithoutTime = new Task("Task", "Desc", Status.NEW);

        manager.createTask(taskWithoutTime);

        assertTrue(manager.getPrioritizedTasks().isEmpty(),
                "Задачи без времени не должны попадать в prioritizedTasks");
    }

    @Test
    void epicStatusShouldUpdateWhenSubtasksChange() {

        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, epic.getId());


        int epicId = manager.createEpic(epic);
        int subtaskId = manager.createSubtask(subtask);


        assertEquals(Status.NEW, manager.getEpic(epicId).getStatus(),
                "Начальный статус эпика должен быть NEW");

        Subtask updatedSubtask = new Subtask("Updated", "Desc", Status.DONE, epicId);
        updatedSubtask.setId(subtaskId);
        manager.updateSubtask(updatedSubtask);

        assertEquals(Status.DONE, manager.getEpic(epicId).getStatus(),
                "Статус эпика должен измениться на DONE");
    }

    @Test
    void prioritizedTasksShouldContainOnlyTasksWithTime() {

        Task taskWithoutTime = new Task("Task1", "Desc", Status.NEW);
        Task taskWithTime = new Task("Task2", "Desc", Status.NEW);
        taskWithTime.setStartTime(LocalDateTime.now());


        manager.createTask(taskWithoutTime);
        manager.createTask(taskWithTime);


        Set<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(1, prioritized.size());
        assertTrue(prioritized.contains(taskWithTime));
        assertFalse(prioritized.contains(taskWithoutTime));
    }
}