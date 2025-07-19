package managers;

import model.*;
import java.util.List;

public interface TaskManager {
    List<Task> getTasks();
    List<Epic> getEpics();
    List<Subtask> getSubtasks();

    Task getTask(int id);
    Epic getEpic(int id);
    void getSubtask(int id);

    int createTask(Task task);
    int createEpic(Epic epic);
    int createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void deleteTask(int id);
    void deleteEpic(int id);

    List<Subtask> getEpicSubtasks(int epicId);
    List<Task> getHistory();
}