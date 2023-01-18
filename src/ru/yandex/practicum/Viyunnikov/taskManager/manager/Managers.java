package ru.yandex.practicum.Viyunnikov.taskManager.manager;

import ru.yandex.practicum.Viyunnikov.taskManager.manager.file.FileBackedTasksManager;
import ru.yandex.practicum.Viyunnikov.taskManager.manager.task.InMemoryTaskManager;
import ru.yandex.practicum.Viyunnikov.taskManager.manager.task.TaskManager;
import ru.yandex.practicum.Viyunnikov.taskManager.manager.history.HistoryManager;
import ru.yandex.practicum.Viyunnikov.taskManager.manager.history.InMemoryHistoryManager;

import java.io.File;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTask() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager getDefaultFileBackedTasksManager(File file) {
        return new FileBackedTasksManager(file);
    }

}