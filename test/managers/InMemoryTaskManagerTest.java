package managers;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        updated.setStartTime(LocalDateTime.now().plusHours(3));
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

    @Test
    void epicStatusShouldUpdateWhenSubtasksChange() {
        int epicId = manager.createEpic(epic);
        int subId1 = manager.createSubtask(subtask1);
        int subId2 = manager.createSubtask(subtask2);

        // Все подзадачи NEW
        assertEquals(Status.NEW, manager.getEpic(epicId).getStatus());

        // NEW и DONE
        Subtask updated1 = new Subtask("Updated", "Desc", Status.DONE, epicId);
        updated1.setId(subId1);
        manager.updateSubtask(updated1);
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicId).getStatus());

        // Все DONE
        Subtask updated2 = new Subtask("Updated", "Desc", Status.DONE, epicId);
        updated2.setId(subId2);
        manager.updateSubtask(updated2);
        assertEquals(Status.DONE, manager.getEpic(epicId).getStatus());

        // Все IN_PROGRESS
        updated1.setStatus(Status.IN_PROGRESS);
        updated2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(updated1);
        manager.updateSubtask(updated2);
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicId).getStatus());
    }

    @Test
    void epicShouldCalculateTimeFieldsCorrectly() {
        int epicId = manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic savedEpic = manager.getEpic(epicId);
        assertEquals(subtask1.getStartTime(), savedEpic.getStartTime());
        assertEquals(subtask2.getEndTime(), savedEpic.getEndTime());
        assertEquals(Duration.ofHours(1), savedEpic.getDuration());
    }

    @Test
    void getPrioritizedTasksShouldReturnSortedTasks() {
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Set<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(3, prioritized.size());
        assertTrue(prioritized.stream().allMatch(t -> t.getStartTime() != null));
    }

    @Test
    void shouldNotAllowTimeOverlaps() {
        manager.createTask(task);

        Task overlappingTask = new Task("Overlapping", "Desc", Status.NEW);
        overlappingTask.setStartTime(task.getStartTime().plusMinutes(15));
        overlappingTask.setDuration(Duration.ofMinutes(30));

        assertThrows(ManagerSaveException.class, () -> manager.createTask(overlappingTask));
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
}