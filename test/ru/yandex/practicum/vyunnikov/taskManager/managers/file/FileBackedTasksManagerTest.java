package ru.yandex.practicum.vyunnikov.taskManager.managers.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vyunnikov.taskManager.managers.task.InMemoryTaskManager;
import ru.yandex.practicum.vyunnikov.taskManager.managers.task.TaskManagerTest;
import ru.yandex.practicum.vyunnikov.taskManager.task.Epic;
import ru.yandex.practicum.vyunnikov.taskManager.task.Status;
import ru.yandex.practicum.vyunnikov.taskManager.task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.Duration.ofMinutes;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    File file = Path.of("src", "ru", "yandex", "practicum", "vyunnikov",
            "taskManager", "task", "file.csv").toFile();

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager(file);
    }

    @AfterEach
    public void afterEach() {
        try {
            new FileWriter(file, false).close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void saveAndLoadFromFile_allAddedTasksWillLoadCorrectly_underNormalConditions() {
        Task task = new Task(Status.NEW, "Task1", "описание1",
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000));
        manager.createTask(task);
        Epic epic = new Epic(Status.NEW, "Epic1", "описание1",
                LocalDateTime.of(2023, 1, 2, 0, 0, 0), ofMinutes(0));
        manager.createEpic(epic);
        manager.getTask(1);
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        fileManager.loadFromFile(file);
        Task task1 = ((List<Task>) manager.getAllTask()).get(0);
        Epic epic1 = ((List<Epic>) manager.getAllEpic()).get(0);

        Assertions.assertEquals(List.of(task), manager.getAllTask());
        assertEquals(task.getId(), task1.getId(), "Id задач не совпадают.");
        assertEquals(task.getTitle(), task1.getTitle(), "Названия задач не совпадают.");
        assertEquals(task.getDescription(), task1.getDescription(), "Описания задач не совпадают.");
        assertEquals(task.getDuration(), task1.getDuration(), "Продолжительности задач не совпадают.");
        assertEquals(task.getStatus(), task1.getStatus(), "Статусы задач не совпадают.");
        assertEquals(task.getStartTime(), task1.getStartTime(), "Время начала задач не совпадает.");
        assertEquals(task.getEndTime(), task1.getEndTime(), "Время окончания задач не совпадает.");

        Assertions.assertEquals(List.of(epic), manager.getAllEpic());
        assertEquals(epic.getId(), epic1.getId(), "Id задач не совпадают.");
        assertEquals(epic.getTitle(), epic1.getTitle(), "Названия задач не совпадают.");
        assertEquals(epic.getDescription(), epic1.getDescription(), "Описания задач не совпадают.");
        assertEquals(epic.getDuration(), epic1.getDuration(), "Продолжительности задач не совпадают.");
        assertEquals(epic.getStatus(), epic1.getStatus(), "Статусы задач не совпадают.");
        assertEquals(epic.getStartTime(), epic1.getStartTime(), "Время начала задач не совпадает.");
        assertEquals(epic.getEndTime(), epic1.getEndTime(), "Время окончания задач не совпадает.");
    }

    @Test
    public void saveAndLoadFromFile_emptyListTasksEpicsSubtasks_ifThereAreNoCreatedTasks() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        fileManager.save();
        fileManager.loadFromFile(file);
        Assertions.assertEquals(Collections.EMPTY_LIST, manager.getAllTask());
        Assertions.assertEquals(Collections.EMPTY_LIST, manager.getAllEpic());
        Assertions.assertEquals(Collections.EMPTY_LIST, manager.getAllSubtask());
    }

    @Test
    public void saveAndLoadFromFile_emptyHistory_ifThereAreNoCreatedTasks() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        fileManager.save();
        fileManager.loadFromFile(file);
        Assertions.assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }
}
