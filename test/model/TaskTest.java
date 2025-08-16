package model;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void equalById() {
        Task t1 = new Task("T1", "D", Status.NEW);
        Task t2 = new Task("T2", "D", Status.NEW);
        t1.setId(1);
        t2.setId(1);
        assertEquals(t1, t2);
    }

    @Test
    void immutableWhenAdded() {
        Task t = new Task("T", "D", Status.NEW);
        t.setStartTime(LocalDateTime.now());
        assertEquals("T", t.getName());
    }

    @Test
    void calculateEndTime() {
        Task t = new Task("T", "D", Status.NEW);
        LocalDateTime start = LocalDateTime.now();
        t.setStartTime(start);
        t.setDuration(Duration.ofHours(1));
        assertEquals(start.plusHours(1), t.getEndTime());
    }

    @Test
    void nullEndTime() {
        Task t = new Task("T", "D", Status.NEW);
        t.setDuration(Duration.ofMinutes(30));
        assertNull(t.getEndTime());
    }
}