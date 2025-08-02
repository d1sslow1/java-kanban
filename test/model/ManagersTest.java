package model;

import managers.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void getDefaultManager() {
        TaskManager tm = Managers.getDefault();
        assertNotNull(tm);

        Task t = new Task("T", "D", Status.NEW);
        tm.createTask(t);
    }

    @Test
    void getDefaultHistory() {
        HistoryManager hm = Managers.getDefaultHistory();
        assertNotNull(hm);

        Task t = new Task("T", "D", Status.NEW);
        hm.add(t);
        assertFalse(hm.getHistory().isEmpty());
    }
}