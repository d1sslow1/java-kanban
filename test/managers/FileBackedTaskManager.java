package managers;

import model.*;
import org.junit.jupiter.api.*;
import java.io.File;
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
    void shouldSaveAndLoadTasksWithTime() {
        // Создаем тестовые данные
        Task task = new Task("Task", "Desc", Status.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));

        Epic epic = new Epic("Epic", "Epic desc");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Sub", "Sub desc", Status.NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.now().plusHours(1));
        subtask.setDuration(Duration.ofMinutes(45));

        // Сохраняем
        manager.createTask(task);
        manager.createSubtask(subtask);

        // Загружаем
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем
        assertNotNull(loaded.getTask(task.getId()));
        assertEquals(task.getStartTime(), loaded.getTask(task.getId()).getStartTime());

        assertNotNull(loaded.getSubtask(subtask.getId()));
        assertEquals(subtask.getDuration(), loaded.getSubtask(subtask.getId()).getDuration());

        assertNotNull(loaded.getEpic(epic.getId()));
        assertEquals(subtask.getEndTime(), loaded.getEpic(epic.getId()).getEndTime());
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
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));
        manager.createTask(task);

        // Попытка загрузить пересекающуюся задачу
        String corruptData = task.getId() + ",TASK,Overlap,NEW,Desc,," +
                task.getStartTime().plusMinutes(15).format(FileBackedTaskManager.DATE_TIME_FORMATTER) + ",30\n";
        Files.write(tempFile.toPath(), corruptData.getBytes());

        assertThrows(ManagerSaveException.class, () ->
                FileBackedTaskManager.loadFromFile(tempFile));
    }
}