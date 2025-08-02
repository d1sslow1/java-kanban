package model;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void storeEpicId() {
        Subtask s = new Subtask("S", "D", Status.NEW, 1);
        assertEquals(1, s.getEpicId());
    }

    @Test
    void equalById() {
        Subtask s1 = new Subtask("S", "D", Status.NEW, 1);
        Subtask s2 = new Subtask("S", "D", Status.NEW, 1);
        s1.setId(1);
        s2.setId(1);
        assertEquals(s1, s2);
    }

    @Test
    void calculateEndTime() {
        Subtask s = new Subtask("S", "D", Status.NEW, 1);
        LocalDateTime start = LocalDateTime.now();
        s.setStartTime(start);
        s.setDuration(Duration.ofMinutes(30));
        assertEquals(start.plusMinutes(30), s.getEndTime());
    }

    @Test
    void nullEndTime() {
        Subtask s = new Subtask("S", "D", Status.NEW, 1);
        s.setDuration(Duration.ofMinutes(30));
        assertNull(s.getEndTime());
    }
}