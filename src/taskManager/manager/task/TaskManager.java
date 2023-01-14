package taskManager.manager.task;

import taskManager.task.Epic;
import taskManager.task.Subtask;
import taskManager.task.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface TaskManager {

    List<Task> getPrioritizedTasks();

    List<Task> getHistory();

    public void createTask(Task task);

    public void updateTask(Task task);

    public Task getTask(int taskId);

    public void deleteTask(int taskId);

    public void createEpic(Epic epic);

    public void updateEpic(Epic epic);

    public Epic getEpic(int epicId);

    public void deleteEpic(int epicId);

    public void createSubtask(Subtask subtask);

    public void updateSubtask(int subtaskId, Subtask subtask);

    Subtask getSubtask(int subtaskId);

    public ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    public void deleteSubtask(int subtaskId);

    public Collection<Task> getAllTask();

    public Collection<Subtask> getAllSubask();

    public Collection<Epic> getAllEpic();

    public void deleteAllTask();

    public void deleteAllSubtasks();

    public void deleteAllEpics();

}
