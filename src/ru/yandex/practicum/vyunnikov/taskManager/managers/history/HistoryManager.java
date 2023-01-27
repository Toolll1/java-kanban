package ru.yandex.practicum.vyunnikov.taskManager.managers.history;

import ru.yandex.practicum.vyunnikov.taskManager.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}