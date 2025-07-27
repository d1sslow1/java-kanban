package model;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void subtaskStoresEpicId() {
        Subtask sub = new Subtask("Sub", "Desc", Status.NEW, 42);
        assertEquals(42, sub.getEpicId());
    }

    @Test
    void subtaskEqualityById() {
        Subtask s1 = new Subtask("S", "D", Status.NEW, 1);
        Subtask s2 = new Subtask("S", "D", Status.NEW, 1);
        s1.setId(7);
        s2.setId(7);
        assertEquals(s1, s2);
    }

    @Test
    void shouldCalculateEndTimeCorrectly() {
        LocalDateTime start = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(45);

        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, 1);
        subtask.setStartTime(start);
        subtask.setDuration(duration);

        assertEquals(start.plus(duration), subtask.getEndTime());
    }

    @Test
    void shouldReturnNullEndTimeWhenNoStartTime() {
        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, 1);
        subtask.setDuration(Duration.ofMinutes(30));
        assertNull(subtask.getEndTime());
    }
}