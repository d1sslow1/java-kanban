package managers;

import model.*;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager manager;
    private Task t1, t2, t3;

    @BeforeEach
    void setUp() {
        manager = new InMemoryHistoryManager();
        t1 = new Task("T1", "D", Status.NEW);
        t1.setId(1);
        t2 = new Task("T2", "D", Status.IN_PROGRESS);
        t2.setId(2);
        t3 = new Task("T3", "D", Status.DONE);
        t3.setId(3);
    }

    @Test
    void addTasks() {
        manager.add(t1);
        manager.add(t2);
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    void replaceDuplicates() {
        manager.add(t1);
        Task updated = new Task("U", "D", Status.DONE);
        updated.setId(1);
        manager.add(updated);
        assertEquals("U", manager.getHistory().getFirst().getName());
    }

    @Test
    void removeTasks() {
        manager.add(t1);
        manager.add(t2);
        manager.add(t3);

        manager.remove(1);
        assertEquals(List.of(t2, t3), manager.getHistory());

        manager.remove(3);
        assertEquals(List.of(t2), manager.getHistory());
    }

    @Test
    void emptyHistory() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void noDuplicates() {
        manager.add(t1);
        manager.add(t1);
        assertEquals(1, manager.getHistory().size());
    }
}