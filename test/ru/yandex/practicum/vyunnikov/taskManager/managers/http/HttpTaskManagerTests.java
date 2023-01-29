package ru.yandex.practicum.vyunnikov.taskManager.managers.http;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vyunnikov.taskManager.managers.Managers;
import ru.yandex.practicum.vyunnikov.taskManager.managers.task.TaskManagerTest;
import ru.yandex.practicum.vyunnikov.taskManager.servers.HttpTaskServer;
import ru.yandex.practicum.vyunnikov.taskManager.servers.KVServer;
import ru.yandex.practicum.vyunnikov.taskManager.task.Epic;
import ru.yandex.practicum.vyunnikov.taskManager.task.Status;
import ru.yandex.practicum.vyunnikov.taskManager.task.Subtask;
import ru.yandex.practicum.vyunnikov.taskManager.task.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static java.time.Duration.ofMinutes;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTests extends TaskManagerTest<HttpTaskManager> {
    HttpTaskServer httpTaskServer;
    KVServer kVServer;

    @BeforeEach
    public void beforeEach() throws IOException {
        kVServer = new KVServer();
        kVServer.start();

        httpTaskServer = new HttpTaskServer("http://localhost:8078/");
        manager = Managers.getDefault("http://localhost:8078/");
    }

    @AfterEach
    public void afterEach() {
        kVServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void saveAndLoadFromServer_addedTaskWillLoadCorrectly_underNormalConditions() throws IOException {
        Task task = new Task(Status.NEW, "Task1", "описание1",
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000));

        manager.createTask(task);

        httpTaskServer.stop();
        httpTaskServer = new HttpTaskServer("http://localhost:8078/");

        Task task1 = manager.getTask(1);

        assertEquals(task.getTitle(), task1.getTitle(), "Названия задач не совпадают.");
        assertEquals(task.getDescription(), task1.getDescription(), "Описания задач не совпадают.");
        assertEquals(task.getDuration(), task1.getDuration(), "Продолжительности задач не совпадают.");
        assertEquals(task.getStatus(), task1.getStatus(), "Статусы задач не совпадают.");
        assertEquals(task.getStartTime(), task1.getStartTime(), "Время начала задач не совпадает.");
        assertEquals(task.getEndTime(), task1.getEndTime(), "Время окончания задач не совпадает.");
    }

    @Test
    public void saveAndLoadFromServer_addedEpicWillLoadCorrectly_underNormalConditions() throws IOException {
        Epic epic = new Epic(Status.NEW, "Epic1", "описание1",
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5));

        manager.createEpic(epic);

        httpTaskServer.stop();
        httpTaskServer = new HttpTaskServer("http://localhost:8078/");

        Epic epic1 = manager.getEpic(1);

        assertEquals(epic.getTitle(), epic1.getTitle(), "Названия эпиков не совпадают.");
        assertEquals(epic.getDescription(), epic1.getDescription(), "Описания эпиков не совпадают.");
        assertEquals(epic.getDuration(), epic1.getDuration(), "Продолжительности эпиков не совпадают.");
        assertEquals(epic.getStatus(), epic1.getStatus(), "Статусы эпиков не совпадают.");
        assertEquals(epic.getStartTime(), epic1.getStartTime(), "Время начала эпиков не совпадает.");
        assertEquals(epic.getEndTime(), epic1.getEndTime(), "Время окончания эпиков не совпадает.");
    }

    @Test
    public void saveAndLoadFromServer_addedSubtaskWillLoadCorrectly_underNormalConditions() throws IOException {
        Subtask subtask = new Subtask(Status.NEW, "Subtask1.1", "описание1", 1,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5));

        manager.createEpic(new Epic(Status.NEW, "Epic1", "описание1",
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5)));
        manager.createSubtask(subtask);

        httpTaskServer.stop();
        httpTaskServer = new HttpTaskServer("http://localhost:8078/");

        Subtask subtask1 = manager.getSubtask(2);

        assertEquals(subtask.getEpicId(), subtask1.getEpicId(), "Id эпиков у подзадач не совпадают.");
        assertEquals(subtask.getTitle(), subtask1.getTitle(), "Названия подзадач не совпадают.");
        assertEquals(subtask.getDescription(), subtask1.getDescription(), "Описания подзадач не совпадают.");
        assertEquals(subtask.getDuration(), subtask1.getDuration(), "Продолжительности подзадач не совпадают.");
        assertEquals(subtask.getStatus(), subtask1.getStatus(), "Статусы подзадач не совпадают.");
        assertEquals(subtask.getStartTime(), subtask1.getStartTime(), "Время начала подзадач не совпадает.");
        assertEquals(subtask.getEndTime(), subtask1.getEndTime(), "Время окончания подзадач не совпадает.");
    }

    @Test
    public void saveAndLoadFromServer_emptyListTasksEpicsSubtasks_ifThereAreNoCreatedTasks() {
        manager.save();
        manager.loadFromServer();
        assertEquals(Collections.EMPTY_LIST, manager.getAllTask());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpic());
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtask());
    }
}