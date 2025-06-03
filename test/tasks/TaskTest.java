package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void taskShouldNotChangeWhenAddedToManager() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Original", "Desc", Status.NEW);
        int id = manager.createTask(task);

        Task saved = manager.getTask(id);
        assertEquals("Original", saved.getName());
        assertEquals("Desc", saved.getDescription());
        assertEquals(Status.NEW, saved.getStatus());
    }
}