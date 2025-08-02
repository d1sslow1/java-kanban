package model;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void emptySubtasks() {
        Epic e = new Epic("E", "D");
        assertTrue(e.getSubtaskIds().isEmpty());
    }

    @Test
    void addRemoveSubtask() {
        Epic e = new Epic("E", "D");
        e.addSubtaskId(1);
        e.removeSubtaskId(1);
        assertFalse(e.getSubtaskIds().contains(1));
    }

    @Test
    void equalById() {
        Epic e1 = new Epic("E", "D");
        Epic e2 = new Epic("E", "D");
        e1.setId(1);
        e2.setId(1);
        assertEquals(e1, e2);
    }

    @Test
    void calculateTime() {
        Epic e = new Epic("E", "D");
        Subtask s1 = new Subtask("S1", "D", Status.NEW, 1);
        s1.setStartTime(LocalDateTime.now());
        s1.setDuration(Duration.ofMinutes(30));

        e.updateTimeFields(List.of(s1));
        assertEquals(s1.getStartTime(), e.getStartTime());
    }

    @Test
    void emptyTime() {
        Epic e = new Epic("E", "D");
        e.updateTimeFields(List.of());
        assertNull(e.getStartTime());
    }
}