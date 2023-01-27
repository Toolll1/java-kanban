package ru.yandex.practicum.vyunnikov.taskManager.managers.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vyunnikov.taskManager.managers.Managers;
import ru.yandex.practicum.vyunnikov.taskManager.managers.http.adapters.DurationAdapter;
import ru.yandex.practicum.vyunnikov.taskManager.managers.http.adapters.LocalDateAdapter;
import ru.yandex.practicum.vyunnikov.taskManager.servers.HttpTaskServer;
import ru.yandex.practicum.vyunnikov.taskManager.servers.KVServer;
import ru.yandex.practicum.vyunnikov.taskManager.task.Epic;
import ru.yandex.practicum.vyunnikov.taskManager.task.Status;
import ru.yandex.practicum.vyunnikov.taskManager.task.Subtask;
import ru.yandex.practicum.vyunnikov.taskManager.task.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.Duration.ofMinutes;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTests {
    HttpTaskServer httpTaskServer;
    KVServer kVServer;
    HttpTaskManager taskManager;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    public void beforeEach() throws IOException {
        kVServer = new KVServer();
        kVServer.start();

        httpTaskServer = new HttpTaskServer("http://localhost:8078/");
        taskManager = Managers.getDefaultHttpTaskManager("http://localhost:8078/");
    }

    @AfterEach
    public void afterEach() {
        kVServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void saveAndLoadFromServer_emptyListTasksEpicsSubtasks_ifThereAreNoCreatedTasks() {
        taskManager.save();
        taskManager.loadFromServer();
        Assertions.assertEquals(Collections.EMPTY_LIST, taskManager.getAllTask());
        Assertions.assertEquals(Collections.EMPTY_LIST, taskManager.getAllEpic());
        Assertions.assertEquals(Collections.EMPTY_LIST, taskManager.getAllSubtask());
    }

    @Test
    public void saveAndLoadHistoryFromServer_allAddedTasksWillLoadCorrectly_underNormalConditions() throws IOException {
        Task task = new Task(Status.NEW, "Task1", "описание1",
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000));
        Task task1 = uploadingAHistoryToTheServerAndCallingTheGetMethod(task);

        assertEquals(task.getTitle(), task1.getTitle(), "Названия задач не совпадают.");
        assertEquals(task.getDescription(), task1.getDescription(), "Описания задач не совпадают.");
        assertEquals(task.getDuration(), task1.getDuration(), "Продолжительности задач не совпадают.");
        assertEquals(task.getStatus(), task1.getStatus(), "Статусы задач не совпадают.");
        assertEquals(task.getStartTime(), task1.getStartTime(), "Время начала задач не совпадает.");
        assertEquals(task.getEndTime(), task1.getEndTime(), "Время окончания задач не совпадает.");

    }


    @Test
    public void saveAndLoadFromServer_allAddedTasksWillLoadCorrectly_underNormalConditions() throws IOException {

        Task task = new Task(Status.NEW, "Task1", "описание1",
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000));
        Epic epic = new Epic(Status.NEW, "Epic1", "описание1",
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5));
        Subtask subtask = new Subtask(Status.NEW, "Subtask1.1", "описание1", 2,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5));

        Task task1 = uploadingATaskToTheServerAndDownloadingAfterRestarting(task);
        Epic epic1 = uploadingAEpicToTheServerAndDownloadingAfterRestarting(epic);
        Subtask subtask1 = uploadingSubtaskToTheServerAndDownloadingAfterRestarting(subtask);

        assertEquals(task.getTitle(), task1.getTitle(), "Названия задач не совпадают.");
        assertEquals(task.getDescription(), task1.getDescription(), "Описания задач не совпадают.");
        assertEquals(task.getDuration(), task1.getDuration(), "Продолжительности задач не совпадают.");
        assertEquals(task.getStatus(), task1.getStatus(), "Статусы задач не совпадают.");
        assertEquals(task.getStartTime(), task1.getStartTime(), "Время начала задач не совпадает.");
        assertEquals(task.getEndTime(), task1.getEndTime(), "Время окончания задач не совпадает.");

        assertEquals(epic.getTitle(), epic1.getTitle(), "Названия эпиков не совпадают.");
        assertEquals(epic.getDescription(), epic1.getDescription(), "Описания эпиков не совпадают.");
        assertEquals(epic.getDuration(), epic1.getDuration(), "Продолжительности эпиков не совпадают.");
        assertEquals(epic.getStatus(), epic1.getStatus(), "Статусы эпиков не совпадают.");
        assertEquals(epic.getStartTime(), epic1.getStartTime(), "Время начала эпиков не совпадает.");
        assertEquals(epic.getEndTime(), epic1.getEndTime(), "Время окончания эпиков не совпадает.");

        assertEquals(subtask.getEpicId(), subtask1.getEpicId(), "Id подзадач не совпадают.");
        assertEquals(subtask.getTitle(), subtask1.getTitle(), "Названия подзадач не совпадают.");
        assertEquals(subtask.getDescription(), subtask1.getDescription(), "Описания подзадач не совпадают.");
        assertEquals(subtask.getDuration(), subtask1.getDuration(), "Продолжительности подзадач не совпадают.");
        assertEquals(subtask.getStatus(), subtask1.getStatus(), "Статусы подзадач не совпадают.");
        assertEquals(subtask.getStartTime(), subtask1.getStartTime(), "Время начала подзадач не совпадает.");
        assertEquals(subtask.getEndTime(), subtask1.getEndTime(), "Время окончания подзадач не совпадает.");
    }

    private Subtask uploadingSubtaskToTheServerAndDownloadingAfterRestarting(Subtask subtask) throws IOException {
        try {
            String postSerialized = gson.toJson(subtask);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(postSerialized))
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/subtask"))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        httpTaskServer.stop();
        httpTaskServer = new HttpTaskServer("http://localhost:8078/");

        String result = "";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/subtask"))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            result = response.body();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Type tasksType = new TypeToken<List<Subtask>>() {
        }.getType();

        List<Subtask> allSubtasks = gson.fromJson(result, tasksType);

        return allSubtasks.get(0);
    }

    private Epic uploadingAEpicToTheServerAndDownloadingAfterRestarting(Epic epic) throws IOException {
        try {
            String postSerialized = gson.toJson(epic);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(postSerialized))
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/epic"))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        httpTaskServer.stop();
        httpTaskServer = new HttpTaskServer("http://localhost:8078/");

        String result = "";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/epic"))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            result = response.body();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Type tasksType = new TypeToken<List<Epic>>() {
        }.getType();

        List<Epic> allEpics = gson.fromJson(result, tasksType);

        return allEpics.get(0);
    }


    private Task uploadingATaskToTheServerAndDownloadingAfterRestarting(Task task) throws IOException {
        try {
            String postSerialized = gson.toJson(task);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(postSerialized))
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/task"))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        httpTaskServer.stop();
        httpTaskServer = new HttpTaskServer("http://localhost:8078/");

        String result = "";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/task"))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            result = response.body();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Type tasksType = new TypeToken<List<Task>>() {
        }.getType();

        List<Task> allTasks = gson.fromJson(result, tasksType);
        return allTasks.get(0);
    }

    private Task uploadingAHistoryToTheServerAndCallingTheGetMethod(Task task) {
        try {
            String postSerialized = gson.toJson(task);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(postSerialized))
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/task"))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = "";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/history"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            result = response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Type tasksType = new TypeToken<List<Task>>() {
        }.getType();

        List<Task> allTasks = gson.fromJson(result, tasksType);
        return allTasks.get(0);
    }

}
