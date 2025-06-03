package tasks;

import org.junit.jupiter.api.Test;
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
}
