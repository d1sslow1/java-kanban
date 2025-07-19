package managers;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void createTaskShouldAddTaskToManager() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        int taskId = taskManager.createTask(task);
        Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void updateTaskShouldChangeTaskFields() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        int taskId = taskManager.createTask(task);

        Task updatedTask = new Task("Обновленная", "Новое описание", Status.DONE);
        updatedTask.setId(taskId);
        taskManager.updateTask(updatedTask);

        Task savedTask = taskManager.getTask(taskId);
        assertEquals("Обновленная", savedTask.getName(), "Название не обновлено.");
        assertEquals("Новое описание", savedTask.getDescription(), "Описание не обновлено.");
        assertEquals(Status.DONE, savedTask.getStatus(), "Статус не обновлен.");
    }

    @Test
    void deleteTaskShouldRemoveTaskFromManager() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        int taskId = taskManager.createTask(task);
        taskManager.deleteTask(taskId);

        assertNull(taskManager.getTask(taskId), "Задача не удалена.");
    }

    @Test
    void getHistoryShouldReturnViewedTasks() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        int taskId = taskManager.createTask(task);
        taskManager.getTask(taskId);

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История неверного размера.");
        assertEquals(taskId, history.get(0).getId(), "Неверная задача в истории.");
    }

    @Test
    void epicStatusShouldUpdateWhenSubtasksChange() {
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", Status.NEW, epicId);
        int subtaskId = taskManager.createSubtask(subtask);

        assertEquals(Status.NEW, taskManager.getEpic(epicId).getStatus(), "Неверный статус эпика.");

        Subtask updatedSubtask = new Subtask("Обновленная", "Описание", Status.DONE, epicId);
        updatedSubtask.setId(subtaskId);
        taskManager.updateSubtask(updatedSubtask);

        assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus(), "Статус эпика не обновился.");
    }
}