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
        testEpic.setId(manager.createEpic(testEpic));

        testSubtask = new Subtask("Test Subtask", "Subtask Description", Status.NEW, testEpic.getId());
        testSubtask.setId(manager.createSubtask(testSubtask));

        testTask = new Task("Test Task", "Task Description", Status.NEW);
        testTask.setStartTime(LocalDateTime.now());
        testTask.setDuration(Duration.ofMinutes(30));
        testTask.setId(manager.createTask(testTask));
    }

    @Test
    void shouldPrioritizeTasksByStartTime() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();

        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        task1.setStartTime(LocalDateTime.now().plusHours(2));
        task1.setDuration(Duration.ofMinutes(30));
        manager.createTask(task1);

        Task task2 = new Task("Task 2", "Desc", Status.NEW);
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        task2.setDuration(Duration.ofMinutes(30));
        manager.createTask(task2);

        Set<Task> prioritized = manager.getPrioritizedTasks();
        List<Task> prioritizedList = new ArrayList<>(prioritized);

        assertEquals(2, prioritizedList.size());
        assertEquals(task2, prioritizedList.get(0));
        assertEquals(task1, prioritizedList.get(1));
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = manager.createEpic(epic);
        epic.setId(epicId);

        Subtask invalidSubtask = new Subtask("Invalid", "Desc", Status.NEW, epicId);
        invalidSubtask.setId(epicId);

        assertThrows(IllegalArgumentException.class, () -> manager.createSubtask(invalidSubtask));
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
}