package managers;

import model.*;
import org.junit.jupiter.api.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
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
        manager.createSubtask(subtask);

        Epic savedEpic = manager.getEpic(epicId);
        assertNotNull(savedEpic.getStartTime());
        assertEquals(subtask.getStartTime(), savedEpic.getStartTime());
        assertEquals(subtask.getEndTime(), savedEpic.getEndTime());
    }

    @Test
    void tasksWithoutStartTimeShouldNotBeInPrioritizedList() {
        Task noTimeTask = new Task("No time", "Desc", Status.NEW);
        manager.createTask(noTimeTask);

        Set<Task> prioritized = manager.getPrioritizedTasks();
        assertTrue(prioritized.isEmpty());
    }

    @Test
    void epicStatusShouldUpdateWhenSubtasksChange() {
        int epicId = manager.createEpic(epic);
        int subtaskId = manager.createSubtask(subtask);

        Subtask updated = new Subtask("Updated", "Desc", Status.DONE, epicId);
        updated.setId(subtaskId);
        manager.updateSubtask(updated);

        assertEquals(Status.DONE, manager.getEpic(epicId).getStatus());
    }

    @Test
    void createAndGetTaskShouldWorkCorrectly() {
        Task task = new Task("Task", "Desc", Status.NEW);
        task.setStartTime(LocalDateTime.now());
        int taskId = manager.createTask(task);

        Task saved = manager.getTask(taskId);
        assertEquals(task.getName(), saved.getName());
    }
}