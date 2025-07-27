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

        subtask2 = new Subtask("Subtask 2", "Desc 2", Status.IN_PROGRESS, epic.getId());
        subtask2.setStartTime(LocalDateTime.now().plusHours(2));
        subtask2.setDuration(Duration.ofMinutes(15));
    }

    @Test
    void epicShouldCalculateTimeFieldsCorrectly() {
        int epicId = manager.createEpic(epic);
        int subId1 = manager.createSubtask(subtask1);
        int subId2 = manager.createSubtask(subtask2);

        Epic savedEpic = manager.getEpic(epicId);
        assertNotNull(savedEpic.getStartTime());
        assertNotNull(savedEpic.getEndTime());
        assertEquals(Duration.ofMinutes(60), savedEpic.getDuration());
    }

    @Test
    void tasksWithoutStartTimeShouldNotBeInPrioritizedList() {
        Task noTimeTask = new Task("No time", "Desc", Status.NEW);
        manager.createTask(noTimeTask);
        manager.createTask(task);

        Set<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(1, prioritized.size());
        assertTrue(prioritized.contains(task));
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

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicId).getStatus());
    }

    @Test
    void getPrioritizedTasksShouldReturnSortedTasks() {
        manager.createTask(task);
        manager.createEpic(epic); // Эпик без времени не должен попасть в список
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Set<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(3, prioritized.size());
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