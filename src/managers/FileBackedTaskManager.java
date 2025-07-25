package managers;

import model.*;
import java.io.*;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

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
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : super.getTasks()) {
                writer.write(taskToCSV(task) + "\n");
            }

            for (Epic epic : super.getEpics()) {
                writer.write(taskToCSV(epic) + "\n");
            }

            for (Subtask subtask : super.getSubtasks()) {
                writer.write(taskToCSV(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
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
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
    }

    private String taskToCSV(Task task) {
        String type = task instanceof Epic ? "EPIC" :
                task instanceof Subtask ? "SUBTASK" : "TASK";
        String epicId = task instanceof Subtask ?
                String.valueOf(((Subtask) task).getEpicId()) : "";

        return String.join(",",
                String.valueOf(task.getId()),
                type,
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                epicId);
    }

    private Task taskFromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 6) return null;

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        String epicId = parts[5];

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status);
                task.setId(id);
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

    public static void main(String[] args) throws IOException {
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Task 1", "Description 1", Status.NEW);
        manager.createTask(task);

        Epic epic = new Epic("Epic 1", "Description Epic 1");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description Subtask 1", Status.NEW, epicId);
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        System.out.println("Tasks after load: " + loadedManager.getTasks());
        System.out.println("Epics after load: " + loadedManager.getEpics());
        System.out.println("Subtasks after load: " + loadedManager.getSubtasks());
    }
}