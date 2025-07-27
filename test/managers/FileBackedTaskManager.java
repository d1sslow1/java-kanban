package managers;

import model.*;
import org.junit.jupiter.api.*;
import java.io.*;
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
        if (!tempFile.delete()) {
            tempFile.deleteOnExit();
        }
    }

    @Test
    void shouldSaveAndLoadDifferentTaskTypes() {
        // Тест с разными именами задач
        Task regularTask = createTestTask("Regular Task");
        Epic mainEpic = createTestEpic();
        Subtask developmentSubtask = createTestSubtask(mainEpic.getId());

        int taskId = manager.createTask(regularTask);
        int epicId = manager.createEpic(mainEpic);
        int subtaskId = manager.createSubtask(developmentSubtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTaskEquals(regularTask, loadedManager.getTask(taskId));
        assertSubtaskEquals(developmentSubtask, loadedManager.getSubtask(subtaskId));
        assertEpicTimeFields(epicId, developmentSubtask, loadedManager);
    }

    @Test
    void shouldHandleEmptyFile() {
        assertDoesNotThrow(() -> {
            FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
            assertAll(
                    () -> assertTrue(loaded.getTasks().isEmpty(), "Tasks should be empty"),
                    () -> assertTrue(loaded.getEpics().isEmpty(), "Epics should be empty"),
                    () -> assertTrue(loaded.getSubtasks().isEmpty(), "Subtasks should be empty")
            );
        });
    }

    @Test
    void shouldDetectTimeConflicts() throws IOException {
        Task designTask = createTestTask("Design Phase");
        manager.createTask(designTask);

        Task conflictingTask = new Task("Implementation", "Desc", Status.NEW);
        conflictingTask.setId(999);
        conflictingTask.setStartTime(designTask.getStartTime().plusMinutes(15));
        conflictingTask.setDuration(Duration.ofMinutes(30));

        writeToFile(designTask, conflictingTask);

        assertThrows(ManagerSaveException.class,
                () -> FileBackedTaskManager.loadFromFile(tempFile),
                "Should detect time conflict");
    }

    // Улучшенные фабричные методы с уникальными именами
    private Task createTestTask(String uniqueTaskName) {
        Task task = new Task(uniqueTaskName, "Task Description", Status.NEW);
        task.setStartTime(LocalDateTime.now().withNano(0));
        task.setDuration(Duration.ofMinutes(45));
        return task;
    }

    private Epic createTestEpic() {
        return new Epic("Main Epic", "Epic Description");
    }

    private Subtask createTestSubtask(int epicId) {
        Subtask subtask = new Subtask("Development Task", "Subtask Desc", Status.NEW, epicId);
        subtask.setStartTime(LocalDateTime.now().plusHours(1).withNano(0));
        subtask.setDuration(Duration.ofMinutes(60));
        return subtask;
    }

    private void writeToFile(Task... tasks) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile.toPath())) {
            writer.write(String.join(",",
                    "id", "type", "name", "status", "description", "epic", "startTime", "duration"));
            writer.newLine();

            for (Task task : tasks) {
                writer.write(manager.taskToCSV(task));
                writer.newLine();
            }
        }
    }

    private void assertTaskEquals(Task expected, Task actual) {
        assertAll(
                () -> assertNotNull(actual, "Task should not be null"),
                () -> assertEquals(expected.getName(), actual.getName(), "Task names should match"),
                () -> assertEquals(expected.getStartTime(), actual.getStartTime(), "Start times should match"),
                () -> assertEquals(expected.getDuration(), actual.getDuration(), "Durations should match")
        );
    }

    private void assertSubtaskEquals(Subtask expected, Subtask actual) {
        assertAll(
                () -> assertNotNull(actual, "Subtask should not be null"),
                () -> assertEquals(expected.getName(), actual.getName(), "Subtask names should match"),
                () -> assertEquals(expected.getStartTime(), actual.getStartTime(), "Start times should match"),
                () -> assertEquals(expected.getEpicId(), actual.getEpicId(), "Epic IDs should match")
        );
    }

    private void assertEpicTimeFields(int epicId, Subtask subtask, FileBackedTaskManager manager) {
        Epic epic = manager.getEpic(epicId);
        assertAll(
                () -> assertNotNull(epic.getStartTime(), "Epic start time should not be null"),
                () -> assertEquals(subtask.getStartTime(), epic.getStartTime(),
                        "Epic start time should match subtask"),
                () -> assertEquals(subtask.getEndTime(), epic.getEndTime(),
                        "Epic end time should match subtask")
        );
    }
}