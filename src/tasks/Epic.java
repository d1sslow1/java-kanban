package tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {
    private static final Map<Integer, Epic> epics = new HashMap<>();
    private static final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final List<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subtaskIds = new ArrayList<>();
        epics.put(this.getId(), this);
    }

    // Статические методы для управления эпиками
    public static List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public static void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public static Epic getEpicById(int id) {
        return epics.get(id);
    }

    public static void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic savedEpic = epics.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
            savedEpic.updateStatus();
        }
    }

    public static void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.subtaskIds) {
                subtasks.remove(subtaskId);
            }
        }
    }

    // Статические методы для управления подзадачами
    public static List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public static void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtaskIds.clear();
            epic.setStatus(Status.NEW);
        }
    }

    public static Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Subtask createSubtask(String name, String description, Status status) {
        Subtask subtask = new Subtask(name, description, status, this.getId());
        subtasks.put(subtask.getId(), subtask);
        this.subtaskIds.add(subtask.getId());
        this.updateStatus();
        return subtask;
    }

    public static void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.updateStatus();
            }
        }
    }

    public static void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.subtaskIds.remove((Integer) id);
                epic.updateStatus();
            }
        }
    }

    // Методы для работы с подзадачами эпика
    public List<Subtask> getSubtasks() {
        List<Subtask> result = new ArrayList<>();
        for (int subtaskId : subtaskIds) {
            result.add(subtasks.get(subtaskId));
        }
        return result;
    }

    private void updateStatus() {
        if (subtaskIds.isEmpty()) {
            this.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;

            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            this.setStatus(Status.NEW);
        } else if (allDone) {
            this.setStatus(Status.DONE);
        } else {
            this.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}