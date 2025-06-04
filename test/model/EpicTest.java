package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void epicShouldStartWithEmptySubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        assertTrue(epic.getSubtaskIds().isEmpty());
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
}
