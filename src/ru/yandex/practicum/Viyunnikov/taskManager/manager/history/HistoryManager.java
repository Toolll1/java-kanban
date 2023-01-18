package ru.yandex.practicum.Viyunnikov.taskManager.manager.history;

import ru.yandex.practicum.Viyunnikov.taskManager.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}