package ru.yandex.practicum.vyunnikov.taskManager;

import ru.yandex.practicum.vyunnikov.taskManager.servers.HttpTaskServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

       // new KVServer().start();
        HttpTaskServer server = new HttpTaskServer("http://localhost:8078/");
    }
}