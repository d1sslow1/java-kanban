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
    private Task task;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);
        epic.setId(epicId);

        subtask = new Subtask("Subtask", "Desc", Status.NEW, epicId);
        int subtaskId = manager.createSubtask(subtask);
        subtask.setId(subtaskId);

        task = new Task("Task", "Desc", Status.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));
        int taskId = manager.createTask(task);
        task.setId(taskId);
    }

    @Test
    void prioritizeTasks() {
        TaskManager tm = Managers.getDefault();

        Task t1 = new Task("T1", "D", Status.NEW);
        t1.setStartTime(LocalDateTime.now().plusHours(1));
        tm.createTask(t1);

        Task t2 = new Task("T2", "D", Status.NEW);
        t2.setStartTime(LocalDateTime.now().plusHours(2));
        tm.createTask(t2);

        List<Task> prioritized = new ArrayList<>(tm.getPrioritizedTasks());
        assertEquals(2, prioritized.size());
        assertEquals(t1.getName(), prioritized.getFirst().getName());
    }

    @Test
    void rejectSelfEpicSubtask() {
        Epic e = new Epic("E", "D");
        int eId = manager.createEpic(e);

        Subtask s = new Subtask("S", "D", Status.NEW, eId);
        s.setId(eId);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> manager.createSubtask(s));

        assertEquals("Подзадача не может ссылаться на саму себя", ex.getMessage());
    }

    @Test
    void createAndGetTask() {
        Task t = manager.getTask(task.getId());
        assertNotNull(t);
        assertEquals(task.getName(), t.getName());
    }

    @Test
    void createAndGetEpic() {
        Epic e = manager.getEpic(epic.getId());
        assertNotNull(e);
        assertEquals(1, e.getSubtaskIds().size());
    }

    @Test
    void createAndGetSubtask() {
        Subtask s = manager.getSubtask(subtask.getId());
        assertNotNull(s);
        assertEquals(epic.getId(), s.getEpicId());
    }

    @Test
    void updateTaskStatus() {
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, manager.getTask(task.getId()).getStatus());
    }

    @Test
    void updateEpicStatus() {
        Subtask s = new Subtask("S", "D", Status.DONE, epic.getId());
        manager.createSubtask(s);
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void deleteTask() {
        manager.deleteTask(task.getId());
        assertNull(manager.getTask(task.getId()));
    }

    @Test
    void deleteEpicWithSubtasks() {
        manager.deleteEpic(epic.getId());
        assertNull(manager.getEpic(epic.getId()));
    }

    @Test
    void emptySubtasksForInvalidEpic() {
        assertTrue(manager.getEpicSubtasks(999).isEmpty());
    }

    @Test
    void detectOverlap() {
        Task t = new Task("T", "D", Status.NEW);
        t.setStartTime(task.getStartTime().plusMinutes(10));
        t.setDuration(Duration.ofMinutes(20));
        assertTrue(manager.isTaskOverlapping(t));
    }

    @Test
    void addToHistory() {
        manager.getTask(task.getId());
        manager.getEpic(epic.getId());
        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
    }
}