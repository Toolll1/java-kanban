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
import ru.yandex.practicum.vyunnikov.taskManager.managers.task.InMemoryTaskManager;
import ru.yandex.practicum.vyunnikov.taskManager.managers.task.TaskManagerTest;
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

public class HttpTaskManagerTests extends TaskManagerTest<InMemoryTaskManager> {
    HttpTaskServer httpTaskServer;
    KVServer kVServer;

    HttpTaskManager taskManager;

    Task task = new Task(Status.NEW, "Task1", "описание1",
            LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000));
    Epic epic = new Epic(Status.NEW, "Epic1", "описание1",
            LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5));
    Subtask subtask = new Subtask(Status.NEW, "Subtask1.1", "описание1", 1,
            LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5));

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
        taskManager = Managers.getDefault("http://localhost:8078/");
    }

    @AfterEach
    public void afterEach() {
        kVServer.stop();
        httpTaskServer.stop();
    }

    @Override
    public void createTask_setNewId_existIdInObject() throws IOException{
        Task task1 = uploadingATaskToTheServerAndDownloadingAfterRestarting(task);

        assertEquals(task.getTitle(), task1.getTitle(), "Названия задач не совпадают.");
        assertEquals(task.getDescription(), task1.getDescription(), "Описания задач не совпадают.");
        assertEquals(task.getDuration(), task1.getDuration(), "Продолжительности задач не совпадают.");
        assertEquals(task.getStatus(), task1.getStatus(), "Статусы задач не совпадают.");
        assertEquals(task.getStartTime(), task1.getStartTime(), "Время начала задач не совпадает.");
        assertEquals(task.getEndTime(), task1.getEndTime(), "Время окончания задач не совпадает.");
    }

    @Override
    public void createEpic_setNewId_existIdInObject() throws IOException{
        Epic epic1 = uploadingAEpicToTheServerAndDownloadingAfterRestarting(epic);

        assertEquals(epic.getTitle(), epic1.getTitle(), "Названия эпиков не совпадают.");
        assertEquals(epic.getDescription(), epic1.getDescription(), "Описания эпиков не совпадают.");
        assertEquals(epic.getDuration(), epic1.getDuration(), "Продолжительности эпиков не совпадают.");
        assertEquals(epic.getStatus(), epic1.getStatus(), "Статусы эпиков не совпадают.");
        assertEquals(epic.getStartTime(), epic1.getStartTime(), "Время начала эпиков не совпадает.");
        assertEquals(epic.getEndTime(), epic1.getEndTime(), "Время окончания эпиков не совпадает.");
    }

    @Override
    public void createSubtask_setNewId_existIdInObject() throws IOException{
        Epic epic1 = uploadingAEpicToTheServerAndDownloadingAfterRestarting(epic);
        Subtask subtask1 = uploadingSubtaskToTheServerAndDownloadingAfterRestarting(subtask);

        assertEquals(subtask.getEpicId(), subtask1.getEpicId(), "Id подзадач не совпадают.");
        assertEquals(subtask.getTitle(), subtask1.getTitle(), "Названия подзадач не совпадают.");
        assertEquals(subtask.getDescription(), subtask1.getDescription(), "Описания подзадач не совпадают.");
        assertEquals(subtask.getDuration(), subtask1.getDuration(), "Продолжительности подзадач не совпадают.");
        assertEquals(subtask.getStatus(), subtask1.getStatus(), "Статусы подзадач не совпадают.");
        assertEquals(subtask.getStartTime(), subtask1.getStartTime(), "Время начала подзадач не совпадает.");
        assertEquals(subtask.getEndTime(), subtask1.getEndTime(), "Время окончания подзадач не совпадает.");
    }

    @Test
    public void saveAndLoadFromServer_emptyListTasksEpicsSubtasks_ifThereAreNoCreatedTasks() {
        taskManager.save();
        taskManager.loadFromServer();
        Assertions.assertEquals(Collections.EMPTY_LIST, taskManager.getAllTask());
        Assertions.assertEquals(Collections.EMPTY_LIST, taskManager.getAllEpic());
        Assertions.assertEquals(Collections.EMPTY_LIST, taskManager.getAllSubtask());
    }

    @Override
    public void getHistory_returnsTheCorrectTaskList_AfterCallingTheMethodGetTask() {
        Task taskToCheck = uploadingAHistoryToTheServerAndCallingTheGetMethod(task);

        assertEquals(task.getTitle(), taskToCheck.getTitle(), "Названия задач не совпадают.");
        assertEquals(task.getDescription(), taskToCheck.getDescription(), "Описания задач не совпадают.");
        assertEquals(task.getDuration(), taskToCheck.getDuration(), "Продолжительности задач не совпадают.");
        assertEquals(task.getStatus(), taskToCheck.getStatus(), "Статусы задач не совпадают.");
        assertEquals(task.getStartTime(), taskToCheck.getStartTime(), "Время начала задач не совпадает.");
        assertEquals(task.getEndTime(), taskToCheck.getEndTime(), "Время окончания задач не совпадает.");
    }

    @Override
    public void updateTask_statusToInProgress_whenAdjustingTheTaskStatus() {
        Task taskToInProgress = new Task(Status.IN_PROGRESS, "Task1", "описание1",
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000));
        Task taskToCheck = createEndUpdateTask(task, taskToInProgress);

        assertEquals(Status.IN_PROGRESS, taskToCheck.getStatus());
    }

    @Override
    public void updateEpic_statusToInProgress_whenAdjustingTheEpicStatus() {
        Epic epicToInProgress = new Epic(Status.IN_PROGRESS, "Epic1", "описание1",
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5));
        Epic epicToChec = createEndUpdateEpic(epic, epicToInProgress);

        assertEquals(Status.IN_PROGRESS, epicToChec.getStatus());
    }

    @Override
    public void updateSubtask_statusToInProgress_whenAdjustingTheSubtaskStatus() {
        Subtask subtaskToInProgress = new Subtask(Status.IN_PROGRESS, "Subtask1.1", "описание1", 2, 1,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5));
        Subtask subtaskToCheck = createEndUpdateSubtask(epic, subtask, subtaskToInProgress);

        assertEquals(Status.IN_PROGRESS, subtaskToCheck.getStatus());
    }

    @Override
    public void updateTask_statusToInDone_whenAdjustingTheTaskStatus() {
        Task taskToInProgress = new Task(Status.DONE, "Task1", "описание1",
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000));
        Task taskToCheck = createEndUpdateTask(task, taskToInProgress);

        assertEquals(Status.DONE, taskToCheck.getStatus());
    }

    @Override
    public void updateEpic_statusToInDone_whenAdjustingTheEpicStatus() {
        Epic epicToInProgress = new Epic(Status.DONE, "Epic1", "описание1",
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5));
        Epic epicToCheck = createEndUpdateEpic(epic, epicToInProgress);

        assertEquals(Status.DONE, epicToCheck.getStatus());
    }

    @Override
    public void updateSubtask_statusToInDone_whenAdjustingTheSubtaskStatus() {
        Subtask subtaskToInProgress = new Subtask(Status.DONE, "Subtask1.1", "описание1", 2, 1,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5));
        Subtask subtaskToCheck = createEndUpdateSubtask(epic, subtask, subtaskToInProgress);

        assertEquals(Status.DONE, subtaskToCheck.getStatus());
    }

    @Override
    public void createEpic_statusNew_withAnEmptyListOfSubtasks() {
        Epic epicToCheck = savingAnEpicWithASubtaskAndLoadingAnEpic(epic, null, null, null);

        assertEquals(Status.NEW, epicToCheck.getStatus());
    }

    @Override
    public void createEpic_statusNew_ForAllSubtasksWithTheNewStatus() {
        Subtask subtask2 = new Subtask(Status.NEW, "Subtask1.2", "описание2", 1,
                LocalDateTime.of(2023, 1, 14, 16, 30, 0), ofMinutes(5));

        Epic epicToCheck = savingAnEpicWithASubtaskAndLoadingAnEpic(epic, subtask, subtask2, null);

        assertEquals(Status.NEW, epicToCheck.getStatus());
    }

    @Override
    public void createEpic_statusDone_ForAllSubtasksWithTheDoneStatus() {

        Subtask subtask1 = new Subtask(Status.DONE, "Subtask1.1", "описание1", 1,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5));

        Subtask subtask2 = new Subtask(Status.DONE, "Subtask1.1", "описание1", 1,
                LocalDateTime.of(2023, 1, 14, 15, 50, 0), ofMinutes(5));

        Epic epicToCheck = savingAnEpicWithASubtaskAndLoadingAnEpic(epic, subtask1, subtask2, null);

        assertEquals(Status.DONE, epicToCheck.getStatus());
    }

    @Override
    public void createEpic_statusInProgress_ForAllSubtasksWithNewAndDoneStatuses() {
        Subtask subtask2 = new Subtask(Status.DONE, "Subtask1.1", "описание1", 1,
                LocalDateTime.of(2023, 1, 14, 15, 50, 0), ofMinutes(5));
        Epic epicToCheck = savingAnEpicWithASubtaskAndLoadingAnEpic(epic, subtask, subtask2, null);

        assertEquals(Status.IN_PROGRESS, epicToCheck.getStatus());
    }

    @Override
    public void createEpic_statusInProgress_ForAllSubtasksWithNewAndDoneAndIiProgressStatuses() {
        Subtask subtask2 = new Subtask(Status.DONE, "Subtask1.1", "описание1", 1,
                LocalDateTime.of(2023, 1, 14, 15, 50, 0), ofMinutes(5));
        Subtask subtask3 = new Subtask(Status.IN_PROGRESS, "Subtask1.1", "описание1", 1,
                LocalDateTime.of(2023, 1, 10, 15, 50, 0), ofMinutes(5));

        Epic epicToCheck = savingAnEpicWithASubtaskAndLoadingAnEpic(epic, subtask, subtask2, subtask3);

        assertEquals(Status.IN_PROGRESS, epicToCheck.getStatus());
    }

    @Override
    public void updateTask_notUpdate_taskIfNull() {
    }

    @Override
    public void updateEpic_notUpdate_epicIfNull() {
    }

    @Override
    public void updateSubtask_notUpdate_subtaskIfNull() {
    }

    @Override
    public void deleteAllTask_listOfTaskIsEmpty_ifDeleteAllTheTasks() {
        createNewTask(task);
        deleteAllTasks();
        List<Task> tasks = getTasks();

        assertEquals(Collections.EMPTY_LIST, tasks);
    }

    @Override
    public void deleteAllEpics_listOfEpicIsEmpty_ifDeleteAllTheEpics() {
        createNewEpic(epic);
        deleteAllEpics();
        List<Epic> epics = getEpics();

        assertEquals(Collections.EMPTY_LIST, epics);
    }

    @Override
    public void deleteAllSubtasks_listOfSubtaskIsEmpty_ifDeleteAllTheSubtask() {
        createNewEpic(epic);
        createNewSubtask(subtask);
        deleteAllSubtasks();
        List<Subtask> subtasks = getSubtask();

        assertEquals(Collections.EMPTY_LIST, subtasks);
    }

    @Override
    public void deleteSubtask_listOfSubtaskIsEmpty_ifDeleteTheSubtaskById() {
        createNewEpic(epic);
        createNewSubtask(subtask);
        deleteSubtasksById(2);
        List<Subtask> subtasks = getSubtask();

        assertEquals(Collections.EMPTY_LIST, subtasks);
    }

    @Override
    public void deleteTask_listOfTaskIsEmpty_ifDeleteTheTaskById() {
        createNewTask(task);
        deleteTaskById(1);
        List<Task> tasks = getTasks();

        assertEquals(Collections.EMPTY_LIST, tasks);
    }

    @Override
    public void deleteEpic_listOfEpicIsEmpty_ifDeleteTheEpicById() {
        createNewEpic(epic);
        deleteEpicById(1);
        List<Epic> epics = getEpics();

        assertEquals(Collections.EMPTY_LIST, epics);
    }

    @Override
    public void deleteTask_notDeleteTask_IfBadId() {
        createNewTask(task);

        assertEquals(1, getTasks().size());

        deleteTaskById(99);

        assertEquals(1, getTasks().size());
    }

    @Override
    public void deleteEpic_notDeleteEpic_IfBadId() {
        createNewEpic(epic);

        assertEquals(1, getEpics().size());

        deleteEpicById(99);

        assertEquals(1, getEpics().size());
    }

    @Override
    public void deleteSubtask_notDeleteSubtask_IfBadId() {
        createNewEpic(epic);
        createNewSubtask(subtask);

        assertEquals(1, getSubtask().size());

        deleteSubtasksById(99);

        assertEquals(1, getSubtask().size());
    }

    private Subtask createEndUpdateSubtask(Epic epic, Subtask subtask, Subtask subtaskToInProgress) {
        try {
            String postSerialized0 = gson.toJson(epic);
            HttpClient client0 = HttpClient.newHttpClient();
            HttpRequest request0 = HttpRequest
                    .newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(postSerialized0))
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/epic"))
                    .header("Content-Type", "application/json")
                    .build();
            client0.send(request0, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

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

            String postSerialized1 = gson.toJson(subtaskToInProgress);
            HttpClient client1 = HttpClient.newHttpClient();
            HttpRequest request1 = HttpRequest
                    .newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(postSerialized1))
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                    .header("Content-Type", "application/json")
                    .build();
            client1.send(request1, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            HttpClient client2 = HttpClient.newHttpClient();
            HttpRequest request2 = HttpRequest
                    .newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String result = response2.body();

            return gson.fromJson(result, Subtask.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

        String result = "";

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

            HttpClient client0 = HttpClient.newHttpClient();
            HttpRequest request0 = HttpRequest
                    .newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                    .build();
            HttpResponse<String> response0 = client0.send(request0, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            HttpClient client1 = HttpClient.newHttpClient();
            HttpRequest request1 = HttpRequest
                    .newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/history"))
                    .build();

            HttpResponse<String> response1 = client1.send(request1, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            result = response1.body();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Type tasksType = new TypeToken<List<Task>>() {
        }.getType();

        List<Task> allTasks = gson.fromJson(result, tasksType);
        return allTasks.get(0);
    }

    private Task createEndUpdateTask(Task task, Task taskToInProgress) {
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

            String postSerialized1 = gson.toJson(taskToInProgress);
            HttpClient client1 = HttpClient.newHttpClient();
            HttpRequest request1 = HttpRequest
                    .newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(postSerialized1))
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                    .header("Content-Type", "application/json")
                    .build();
            client1.send(request1, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            HttpClient client2 = HttpClient.newHttpClient();
            HttpRequest request2 = HttpRequest
                    .newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));


            String result = response2.body();
            return gson.fromJson(result, Task.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Epic createEndUpdateEpic(Epic epic, Epic epicToInProgress) {
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

            String postSerialized1 = gson.toJson(epicToInProgress);
            HttpClient client1 = HttpClient.newHttpClient();
            HttpRequest request1 = HttpRequest
                    .newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(postSerialized1))
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                    .header("Content-Type", "application/json")
                    .build();
            client1.send(request1, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            HttpClient client2 = HttpClient.newHttpClient();
            HttpRequest request2 = HttpRequest
                    .newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String result = response2.body();

            return gson.fromJson(result, Epic.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Epic savingAnEpicWithASubtaskAndLoadingAnEpic(Epic epic, Subtask subtask, Subtask subtask2, Subtask subtask3) {
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

            if (subtask != null) {
                String postSerializedSubtask = gson.toJson(subtask);
                HttpClient SubtaskClient = HttpClient.newHttpClient();
                HttpRequest requestSubtask = HttpRequest
                        .newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(postSerializedSubtask))
                        .version(HttpClient.Version.HTTP_1_1)
                        .uri(URI.create("http://localhost:8080/tasks/subtask"))
                        .header("Content-Type", "application/json")
                        .build();
                SubtaskClient.send(requestSubtask, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            }

            if (subtask2 != null) {
                String postSerializedSubtask = gson.toJson(subtask2);
                HttpClient SubtaskClient = HttpClient.newHttpClient();
                HttpRequest requestSubtask = HttpRequest
                        .newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(postSerializedSubtask))
                        .version(HttpClient.Version.HTTP_1_1)
                        .uri(URI.create("http://localhost:8080/tasks/subtask"))
                        .header("Content-Type", "application/json")
                        .build();
                SubtaskClient.send(requestSubtask, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            }

            if (subtask3 != null) {
                String postSerializedSubtask = gson.toJson(subtask3);
                HttpClient SubtaskClient = HttpClient.newHttpClient();
                HttpRequest requestSubtask = HttpRequest
                        .newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(postSerializedSubtask))
                        .version(HttpClient.Version.HTTP_1_1)
                        .uri(URI.create("http://localhost:8080/tasks/subtask"))
                        .header("Content-Type", "application/json")
                        .build();
                SubtaskClient.send(requestSubtask, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            }

            HttpClient client2 = HttpClient.newHttpClient();
            HttpRequest request2 = HttpRequest
                    .newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String result = response2.body();

            return gson.fromJson(result, Epic.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Task> getTasks() {
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

        Type taskType = new TypeToken<List<Task>>() {
        }.getType();

        return gson.fromJson(result, taskType);
    }

    private void deleteAllTasks() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .DELETE()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/task"))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNewTask(Task task) {
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
    }

    private List<Epic> getEpics() {
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

        Type epicType = new TypeToken<List<Epic>>() {
        }.getType();

        return gson.fromJson(result, epicType);
    }

    private void deleteAllEpics() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .DELETE()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/epic"))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNewEpic(Epic epic) {
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
    }

    private List<Subtask> getSubtask() {
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

        Type subtaskType = new TypeToken<List<Subtask>>() {
        }.getType();

        return gson.fromJson(result, subtaskType);
    }

    private void deleteAllSubtasks() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .DELETE()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/subtask"))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNewSubtask(Subtask subtask) {
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
    }

    private void deleteTaskById(int id) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .DELETE()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/task/?id=" + id))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteSubtasksById(int id) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .DELETE()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/subtask/?id=" + id))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteEpicById(int id) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .DELETE()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("http://localhost:8080/tasks/epic/?id=" + id))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
