package ru.yandex.practicum.vyunnikov.taskManager.managers;

import ru.yandex.practicum.vyunnikov.taskManager.managers.file.FileBackedTasksManager;
import ru.yandex.practicum.vyunnikov.taskManager.managers.history.HistoryManager;
import ru.yandex.practicum.vyunnikov.taskManager.managers.history.InMemoryHistoryManager;
import ru.yandex.practicum.vyunnikov.taskManager.managers.http.HttpTaskManager;
import ru.yandex.practicum.vyunnikov.taskManager.managers.task.InMemoryTaskManager;
import ru.yandex.practicum.vyunnikov.taskManager.managers.task.TaskManager;

import java.io.File;

public class Managers {
    public static HistoryManager getHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getTask() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager getFileBackedTasksManager(File file) {
        return new FileBackedTasksManager(file);
    }

    public static HttpTaskManager getDefault(String url)  {
        return new HttpTaskManager(url);
    }

}