package ru.yandex.practicum.Viyunnikov.taskManager.manager.task;

import ru.yandex.practicum.Viyunnikov.taskManager.task.Epic;
import ru.yandex.practicum.Viyunnikov.taskManager.task.Subtask;
import ru.yandex.practicum.Viyunnikov.taskManager.task.Task;

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

    public void updateSubtask(Subtask subtask);

    Subtask getSubtask(int subtaskId);

    public ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    public void deleteSubtask(int subtaskId);

    public Collection<Task> getAllTask();

    public Collection<Subtask> getAllSubtask();

    public Collection<Epic> getAllEpic();

    public void deleteAllTask();

    public void deleteAllSubtasks();

    public void deleteAllEpics();

}
