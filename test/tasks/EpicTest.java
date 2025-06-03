package tasks;

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
        Epic e1 = new Epic("E", "D");
        Epic e2 = new Epic("E", "D");
        e1.setId(5);
        e2.setId(5);
        assertEquals(e1, e2);
    }
}
