package ru.yandex.practicum.vyunnikov.taskManager;

import ru.yandex.practicum.vyunnikov.taskManager.api.HttpTaskServer;
import ru.yandex.practicum.vyunnikov.taskManager.manager.Managers;
import ru.yandex.practicum.vyunnikov.taskManager.manager.file.FileBackedTasksManager;
import ru.yandex.practicum.vyunnikov.taskManager.task.Status;
import ru.yandex.practicum.vyunnikov.taskManager.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static java.time.Duration.ofMinutes;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = Path.of("src", "ru", "yandex", "practicum", "vyunnikov",
                "taskManager", "task", "file.csv").toFile();

        FileBackedTasksManager taskManager = Managers.getDefaultFileBackedTasksManager(file);

        taskManager.createTask(new Task(Status.NEW, "Task1", "описание1",
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000)));
        taskManager.createTask(new Task(Status.NEW, "Task2", "описание2", 2,
                LocalDateTime.of(2023, 1, 14, 15, 15, 0), ofMinutes(15)));
        System.out.println(taskManager.getAllTask());

        HttpTaskServer server = new HttpTaskServer(file);

       // System.out.println(taskManager.getAllTask());

    }
}