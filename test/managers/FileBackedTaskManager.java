package managers;

import model.*;
import org.junit.jupiter.api.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
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
        tempFile.delete();
    }

    @Test
    void shouldSaveAndLoadTasksWithTime() throws IOException {
        Task task = new Task("Task", "Desc", Status.NEW);
        task.setStartTime(LocalDateTime.now().withNano(0));
        task.setDuration(Duration.ofMinutes(30));
        int taskId = manager.createTask(task);

        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, epicId);
        subtask.setStartTime(LocalDateTime.now().plusHours(1).withNano(0));
        subtask.setDuration(Duration.ofMinutes(45));
        int subtaskId = manager.createSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        Task loadedTask = loaded.getTask(taskId);
        assertNotNull(loadedTask);
        assertEquals(task.getStartTime(), loadedTask.getStartTime());
        assertEquals(task.getDuration(), loadedTask.getDuration());

        Subtask loadedSubtask = loaded.getSubtask(subtaskId);
        assertNotNull(loadedSubtask);
        assertEquals(subtask.getStartTime(), loadedSubtask.getStartTime());
    }

    @Test
    void shouldLoadEmptyFile() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loaded.getTasks().isEmpty());
        assertTrue(loaded.getPrioritizedTasks().isEmpty());
    }

    @Test
    void shouldHandleTimeOverlapsWhenLoading() throws IOException {
        Task task = new Task("Task", "Desc", Status.NEW);
        task.setStartTime(LocalDateTime.now().withNano(0));
        task.setDuration(Duration.ofMinutes(30));
        manager.createTask(task);

        // Создаем пересекающуюся задачу
        Task overlappingTask = new Task("Overlap", "Desc", Status.NEW);
        overlappingTask.setId(999);
        overlappingTask.setStartTime(task.getStartTime().plusMinutes(15));
        overlappingTask.setDuration(Duration.ofMinutes(30));

        // Сохраняем вручную
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("id,type,name,status,description,epic,startTime,duration\n");
            writer.write(manager.taskToCSV(task) + "\n");
            writer.write(manager.taskToCSV(overlappingTask) + "\n");
        }

        assertThrows(ManagerSaveException.class, () ->
                FileBackedTaskManager.loadFromFile(tempFile));
    }
}