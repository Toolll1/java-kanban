package ru.yandex.practicum.vyunnikov.taskManager;

import ru.yandex.practicum.vyunnikov.taskManager.servers.HttpTaskServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

      //  String url = "http://localhost:8078/";

       // new KVServer().start();
        HttpTaskServer server = new HttpTaskServer("http://localhost:8078/");

        // HttpTaskManager taskManager = Managers.getDefaultHttpTaskManager("http://localhost:8078/");
/*        File file = Path.of("src", "ru", "yandex", "practicum", "vyunnikov",
                "taskManager", "task", "file.csv").toFile();

        FileBackedTasksManager taskManager = Managers.getDefaultFileBackedTasksManager(file);

        taskManager.createTask(new Task(Status.NEW, "Task1", "описание1",
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000)));
        taskManager.createTask(new Task(Status.NEW, "Task2", "описание2", 2,
                LocalDateTime.of(2023, 1, 14, 15, 15, 0), ofMinutes(15)));

        taskManager.createEpic(new Epic(Status.NEW, "Epic1", "описание1",
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5)));

        taskManager.createSubtask(new Subtask(Status.NEW, "Subtask1.1", "описание1", 3,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5)));
        taskManager.createSubtask(new Subtask(Status.NEW, "Subtask1.1", "описание1", 3,
                LocalDateTime.of(2020, 1, 14, 15, 30, 0), ofMinutes(5)));

        taskManager.getTask(1);
        taskManager.getEpic(3);

        System.out.println("История" + taskManager.getHistory());

        HttpTaskServer server = new HttpTaskServer(file);*/
    }
}