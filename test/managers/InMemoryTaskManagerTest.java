package managers;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        task = new Task("Test Task", "Description", Status.NEW);
        epic = new Epic("Test Epic", "Epic Description");
        subtask = new Subtask("Test Subtask", "Subtask Description", Status.NEW, 1);
    }

    @Test
    void shouldAddAndFindTask() {
        final int taskId = taskManager.createTask(task);
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Task not found");
        assertEquals(task, savedTask, "Tasks are not equal");
    }

    @Test
    void shouldUpdateEpicStatusWhenSubtaskChanged() {
        final int epicId = taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(
                new Subtask("Sub", "Desc", Status.NEW, epicId));

        Subtask savedSubtask = taskManager.getSubtask(subtaskId);
        savedSubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubtask);

        assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus());
    }
}