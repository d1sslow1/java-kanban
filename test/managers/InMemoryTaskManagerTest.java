
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
        manager.createSubtask(subtask);

        Epic savedEpic = manager.getEpic(epicId);
        assertNotNull(savedEpic.getStartTime());
        assertEquals(subtask.getStartTime(), savedEpic.getStartTime());
    }

    @Test
    void tasksWithoutStartTimeShouldNotBeInPrioritizedList() {
        Task task = new Task("Task", "Desc", Status.NEW);
        manager.createTask(task);

        assertTrue(manager.getPrioritizedTasks().isEmpty());
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
}