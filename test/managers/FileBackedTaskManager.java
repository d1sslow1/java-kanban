package managers;

import model.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        if (!tempFile.delete()) {
            tempFile.deleteOnExit();
        }
    }

    @Test
    void shouldSaveAndLoadTasks() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, epicId);
        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(Duration.ofMinutes(30));
        manager.createSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Subtask loadedSubtask = loaded.getSubtasks().getFirst();

        assertNotNull(loadedSubtask);
        assertEquals(subtask.getName(), loadedSubtask.getName());
    }
}