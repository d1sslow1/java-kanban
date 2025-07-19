package managers;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void createAndGetTaskShouldWorkCorrectly() {
        Task task = new Task("Task", "Description", Status.NEW);
        int taskId = manager.createTask(task);
        Task savedTask = manager.getTask(taskId);
        assertNotNull(savedTask);
        assertEquals(task.getName(), savedTask.getName());
    }

    @Test
    void updateTaskShouldChangeFields() {
        Task task = new Task("Task", "Desc", Status.NEW);
        int id = manager.createTask(task);
        Task updated = new Task("New", "New desc", Status.DONE);
        updated.setId(id);
        manager.updateTask(updated);
        assertEquals("New", manager.getTask(id).getName());
    }

    @Test
    void deleteTaskShouldRemoveFromManagerAndHistory() {
        Task task = new Task("Task", "Desc", Status.NEW);
        int id = manager.createTask(task);
        manager.getTask(id); // Добавляем в историю
        manager.deleteTask(id);
        assertNull(manager.getTask(id));
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void epicStatusShouldUpdateWhenSubtasksChange() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, epicId);
        int subId = manager.createSubtask(subtask);
        assertEquals(Status.NEW, manager.getEpic(epicId).getStatus());

        Subtask updated = new Subtask("New", "New", Status.DONE, epicId);
        updated.setId(subId);
        manager.updateSubtask(updated);
        assertEquals(Status.DONE, manager.getEpic(epicId).getStatus());
    }
}