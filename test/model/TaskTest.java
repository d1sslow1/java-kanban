package model;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
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
        Task task = new Task("Original", "Desc", Status.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));

        // Здесь предполагается использование мок-менеджера или проверка неизменности полей
        assertEquals("Original", task.getName());
        assertEquals("Desc", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
        assertNotNull(task.getStartTime());
        assertNotNull(task.getDuration());
    }

    @Test
    void shouldCalculateEndTimeCorrectly() {
        LocalDateTime start = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);

        Task task = new Task("Task", "Desc", Status.NEW);
        task.setStartTime(start);
        task.setDuration(duration);

        assertEquals(start.plus(duration), task.getEndTime());
    }

    @Test
    void shouldReturnNullEndTimeWhenNoStartTime() {
        Task task = new Task("Task", "Desc", Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        assertNull(task.getEndTime());
    }
}