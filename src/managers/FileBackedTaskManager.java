package managers;

import model.*;
import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.load();
        return manager;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic,startTime,duration\n");

            for (Task task : getTasks()) {
                writer.write(taskToCSV(task) + "\n");
            }

            for (Epic epic : getEpics()) {
                writer.write(taskToCSV(epic) + "\n");
            }

            for (Subtask subtask : getSubtasks()) {
                writer.write(taskToCSV(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    private void load() {
        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            if (lines.length <= 1) return;

            for (int lineNumber = 1; lineNumber < lines.length; lineNumber++) {
                String currentLine = lines[lineNumber];
                if (currentLine.isEmpty()) continue;

                Task task = taskFromCSV(currentLine);
                if (task != null) {
                    if (task instanceof Epic) {
                        super.createEpic((Epic) task);
                    } else if (task instanceof Subtask) {
                        super.createSubtask((Subtask) task);
                    } else {
                        super.createTask(task);
                    }
                }
            }

            // Обновляем временные поля эпиков после загрузки всех подзадач
            for (Epic epic : getEpics()) {
                updateEpicTimeFields(epic);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла");
        }
    }

    private String taskToCSV(Task task) {
        String type = task instanceof Epic ? "EPIC" :
                task instanceof Subtask ? "SUBTASK" : "TASK";
        String epicId = task instanceof Subtask ?
                String.valueOf(((Subtask) task).getEpicId()) : "";

        String startTime = task.getStartTime() != null ?
                task.getStartTime().format(DATE_TIME_FORMATTER) : "";
        String duration = task.getDuration() != null ?
                String.valueOf(task.getDuration().toMinutes()) : "";

        return String.join(",",
                String.valueOf(task.getId()),
                type,
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                epicId,
                startTime,
                duration);
    }

    private Task taskFromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 8) return null;

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        String epicId = parts[5];
        String startTimeStr = parts[6];
        String durationStr = parts[7];

        LocalDateTime startTime = !startTimeStr.isEmpty() ?
                LocalDateTime.parse(startTimeStr, DATE_TIME_FORMATTER) : null;
        Duration duration = !durationStr.isEmpty() ?
                Duration.ofMinutes(Long.parseLong(durationStr)) : null;

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status);
                task.setId(id);
                task.setStartTime(startTime);
                task.setDuration(duration);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case "SUBTASK":
                if (!epicId.isEmpty()) {
                    Subtask subtask = new Subtask(name, description, status, Integer.parseInt(epicId));
                    subtask.setId(id);
                    subtask.setStartTime(startTime);
                    subtask.setDuration(duration);
                    return subtask;
                }
            default:
                return null;
        }
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}