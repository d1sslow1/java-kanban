package managers;

import model.*;
import org.junit.jupiter.api.*;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;
    private Epic testEpic;
    private Subtask testSubtask;
    private Task testTask;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
        testEpic = new Epic("Test Epic", "Epic Description");
        int epicId = manager.createEpic(testEpic);
        testEpic.setId(epicId);

        testSubtask = new Subtask("Test Subtask", "Subtask Description", Status.NEW, testEpic.getId());
        int subtaskId = manager.createSubtask(testSubtask);
        testSubtask.setId(subtaskId);

        testTask = new Task("Test Task", "Task Description", Status.NEW);
        testTask.setStartTime(LocalDateTime.now());
        testTask.setDuration(Duration.ofMinutes(30));
        int taskId = manager.createTask(testTask);
        testTask.setId(taskId);
    }

    @Test
    void shouldCreateAndRetrieveTask() {
        Task retrievedTask = manager.getTask(testTask.getId());
        assertNotNull(retrievedTask);
        assertEquals(testTask.getName(), retrievedTask.getName());
        assertEquals(testTask.getDescription(), retrievedTask.getDescription());
    }

    @Test
    void shouldCreateAndRetrieveEpic() {
        Epic retrievedEpic = manager.getEpic(testEpic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(testEpic.getName(), retrievedEpic.getName());
        assertEquals(1, retrievedEpic.getSubtaskIds().size());
    }

    @Test
    void shouldCreateAndRetrieveSubtask() {
        Subtask retrievedSubtask = manager.getSubtask(testSubtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals(testSubtask.getName(), retrievedSubtask.getName());
        assertEquals(testEpic.getId(), retrievedSubtask.getEpicId());
    }

    @Test
    void shouldUpdateTaskStatus() {
        testTask.setStatus(Status.IN_PROGRESS);
        manager.updateTask(testTask);

        Task updatedTask = manager.getTask(testTask.getId());
        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    void shouldUpdateEpicStatusBasedOnSubtasks() {
        Subtask newSubtask = new Subtask("New Subtask", "Desc", Status.DONE, testEpic.getId());
        manager.createSubtask(newSubtask);

        Epic updatedEpic = manager.getEpic(testEpic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void shouldDeleteTask() {
        manager.deleteTask(testTask.getId());
        assertNull(manager.getTask(testTask.getId()));
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    void shouldDeleteEpicWithSubtasks() {
        manager.deleteEpic(testEpic.getId());
        assertNull(manager.getEpic(testEpic.getId()));
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    void shouldReturnEmptyListForNonExistentEpicSubtasks() {
        List<Subtask> subtasks = manager.getEpicSubtasks(999);
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void shouldPrioritizeTasksByStartTime() {
        // Очищаем существующие задачи
        manager.deleteAllTasks();

        Task earlyTask = new Task("Early", "Desc", Status.NEW);
        earlyTask.setStartTime(LocalDateTime.now().minusHours(1));
        earlyTask.setDuration(Duration.ofMinutes(30));
        int earlyId = manager.createTask(earlyTask);
        earlyTask.setId(earlyId);

        Task lateTask = new Task("Late", "Desc", Status.NEW);
        lateTask.setStartTime(LocalDateTime.now().plusHours(1));
        lateTask.setDuration(Duration.ofMinutes(30));
        int lateId = manager.createTask(lateTask);
        lateTask.setId(lateId);

        List<Task> prioritized = new ArrayList<>(manager.getPrioritizedTasks());
        assertEquals(2, prioritized.size());
        assertEquals(earlyTask, prioritized.get(0));
        assertEquals(lateTask, prioritized.get(1));
    }

    @Test
    void shouldDetectTaskOverlap() {
        Task overlappingTask = new Task("Overlap", "Desc", Status.NEW);
        overlappingTask.setStartTime(testTask.getStartTime().plusMinutes(10));
        overlappingTask.setDuration(Duration.ofMinutes(20));

        assertTrue(manager.isTaskOverlapping(overlappingTask));
    }

    @Test
    void shouldAddTasksToHistory() {
        manager.getTask(testTask.getId());
        manager.getEpic(testEpic.getId());

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        assertTrue(history.contains(testTask));
        assertTrue(history.contains(testEpic));
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        // Создаем новый эпик и подзадачу с таким же ID
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);

        Subtask invalidSubtask = new Subtask("Invalid", "Desc", Status.NEW, epicId);
        invalidSubtask.setId(epicId); // Устанавливаем тот же ID

        assertThrows(IllegalArgumentException.class, () -> {
            manager.updateSubtask(invalidSubtask); // Пытаемся обновить (не создаем новую)
        });
    }
}