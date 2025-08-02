package managers;

import model.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int nextId = 1;
    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId));

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.values().forEach(prioritizedTasks::remove);
        epics.values().forEach(prioritizedTasks::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.values().forEach(prioritizedTasks::remove);
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
            updateEpicTimeFields(epic);
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public int createTask(Task task) {
        if (isTaskOverlapping(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей");
        }
        int id = nextId++;
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = nextId++;
        epic.setId(id);
        epics.put(id, epic);
        prioritizedTasks.add(epic);
        return id;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (subtask.getId() == subtask.getEpicId()) {
            throw new IllegalArgumentException("Подзадача не может ссылаться на саму себя");
        }
        if (!epics.containsKey(subtask.getEpicId())) {
            return -1;
        }
        if (isTaskOverlapping(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующей");
        }
        int id = nextId++;
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(id);
        updateEpicStatus(epic);
        updateEpicTimeFields(epic);
        prioritizedTasks.add(subtask);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            prioritizedTasks.remove(oldTask);
            if (isTaskOverlapping(task)) {
                prioritizedTasks.add(oldTask);
                throw new ManagerSaveException("Задача пересекается по времени с существующей");
            }
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic != null) {
            prioritizedTasks.remove(savedEpic);
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
            prioritizedTasks.add(savedEpic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) &&
                epics.containsKey(subtask.getEpicId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId());
            prioritizedTasks.remove(oldSubtask);
            if (isTaskOverlapping(subtask)) {
                prioritizedTasks.add(oldSubtask);
                throw new ManagerSaveException("Подзадача пересекается по времени с существующей");
            }
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            updateEpicStatus(epics.get(subtask.getEpicId()));
            updateEpicTimeFields(epics.get(subtask.getEpicId()));
        }
    }

    void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int id : subtaskIds) {
            Subtask subtask = subtasks.get(id);
            if (subtask == null) continue;

            Status status = subtask.getStatus();
            if (status != Status.NEW) allNew = false;
            if (status != Status.DONE) allDone = false;
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    void updateEpicTimeFields(Epic epic) {
        List<Subtask> epicSubtasks = getEpicSubtasks(epic.getId());
        epic.updateTimeFields(epicSubtasks);
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.remove(subtaskId);
                prioritizedTasks.remove(subtask);
                historyManager.remove(subtaskId);
            }
            prioritizedTasks.remove(epic);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
                updateEpicTimeFields(epic);
            }
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return Collections.emptyList();
        }
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public boolean isTaskOverlapping(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .filter(task -> task != newTask)
                .filter(task -> task.getStartTime() != null && task.getEndTime() != null)
                .anyMatch(existingTask -> isTimeOverlapping(
                        existingTask.getStartTime(),
                        existingTask.getEndTime(),
                        newTask.getStartTime(),
                        newTask.getEndTime()));
    }

    private boolean isTimeOverlapping(LocalDateTime start1, LocalDateTime end1,
                                      LocalDateTime start2, LocalDateTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }
}