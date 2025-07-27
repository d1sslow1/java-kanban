package model;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void epicShouldStartWithEmptySubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        assertTrue(epic.getSubtaskIds().isEmpty());
        assertNull(epic.getStartTime());
        assertNull(epic.getDuration());
        assertNull(epic.getEndTime());
    }

    @Test
    void canAddAndRemoveSubtaskId() {
        Epic epic = new Epic("Epic", "Desc");
        epic.addSubtaskId(1);
        epic.removeSubtaskId(1);
        assertFalse(epic.getSubtaskIds().contains(1));
    }

    @Test
    void epicEqualityById() {
        Epic epic1 = new Epic("E", "D");
        Epic epic2 = new Epic("E", "D");
        epic1.setId(5);
        epic2.setId(5);
        assertEquals(epic1, epic2);
    }

    @Test
    void shouldCalculateTimeFieldsCorrectly() {
        Epic epic = new Epic("Epic", "Desc");
        Subtask sub1 = new Subtask("Sub1", "Desc", Status.NEW, 1);
        sub1.setStartTime(LocalDateTime.now());
        sub1.setDuration(Duration.ofMinutes(30));

        Subtask sub2 = new Subtask("Sub2", "Desc", Status.NEW, 1);
        sub2.setStartTime(LocalDateTime.now().plusHours(1));
        sub2.setDuration(Duration.ofHours(2));

        epic.updateTimeFields(List.of(sub1, sub2));

        assertEquals(sub1.getStartTime(), epic.getStartTime());
        assertEquals(sub2.getEndTime(), epic.getEndTime());
        assertEquals(Duration.ofMinutes(150), epic.getDuration());
    }

    @Test
    void shouldHandleEmptySubtasksForTimeFields() {
        Epic epic = new Epic("Epic", "Desc");
        epic.updateTimeFields(List.of());

        assertNull(epic.getStartTime());
        assertNull(epic.getDuration());
        assertNull(epic.getEndTime());
    }
}