package file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.TaskManagerTest;
import taskManager.manager.file.FileBackedTasksManager;
import taskManager.manager.task.InMemoryTaskManager;
import taskManager.task.Epic;
import taskManager.task.Status;
import taskManager.task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.Duration.*;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    File file = Path.of("src", "taskManager", "task", "file.csv").toFile();

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
    public void correctlySaveAndLoad() {
        Task task = new Task(Status.NEW, "Task1", "описание1",
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000));
        manager.createTask(task);
        Epic epic = new Epic(Status.NEW, "Epic1", "описание1",
                LocalDateTime.of(2023, 1, 2, 0, 0, 0), ofMinutes(0));
        manager.createEpic(epic);
        manager.getTask(1);
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        fileManager.loadFromFile(file);
        Assertions.assertEquals(List.of(task), manager.getAllTask());
        Assertions.assertEquals(List.of(epic), manager.getAllEpic());
    }

    @Test
    public void saveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        fileManager.save();
        fileManager.loadFromFile(file);
        Assertions.assertEquals(Collections.EMPTY_LIST, manager.getAllTask());
        Assertions.assertEquals(Collections.EMPTY_LIST, manager.getAllEpic());
        Assertions.assertEquals(Collections.EMPTY_LIST, manager.getAllSubtask());
    }

    @Test
    public void saveAndLoadEmptyHistory() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        fileManager.save();
        fileManager.loadFromFile(file);
        Assertions.assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }


}
