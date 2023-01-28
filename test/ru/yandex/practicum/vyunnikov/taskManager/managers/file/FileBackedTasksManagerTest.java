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

    @Override
    public void getHistory_returnsTheCorrectTaskList_AfterCallingTheMethodGetTask() {
        super.getHistory_returnsTheCorrectTaskList_AfterCallingTheMethodGetTask();
    }

    @Override
    public void updateTask_statusToInProgress_whenAdjustingTheTaskStatus() {
        super.updateTask_statusToInProgress_whenAdjustingTheTaskStatus();
    }

    @Override
    public void updateEpic_statusToInProgress_whenAdjustingTheEpicStatus() {
        super.updateEpic_statusToInProgress_whenAdjustingTheEpicStatus();
    }

    @Override
    public void updateSubtask_statusToInProgress_whenAdjustingTheSubtaskStatus() {
        super.updateSubtask_statusToInProgress_whenAdjustingTheSubtaskStatus();
    }

    @Override
    public void updateTask_statusToInDone_whenAdjustingTheTaskStatus() {
        super.updateTask_statusToInDone_whenAdjustingTheTaskStatus();
    }

    @Override
    public void updateEpic_statusToInDone_whenAdjustingTheEpicStatus() {
        super.updateEpic_statusToInDone_whenAdjustingTheEpicStatus();
    }

    @Override
    public void updateSubtask_statusToInDone_whenAdjustingTheSubtaskStatus() {
        super.updateSubtask_statusToInDone_whenAdjustingTheSubtaskStatus();
    }

    @Override
    public void createEpic_statusNew_withAnEmptyListOfSubtasks() {
        super.createEpic_statusNew_withAnEmptyListOfSubtasks();
    }

    @Override
    public void createEpic_statusNew_ForAllSubtasksWithTheNewStatus() {
        super.createEpic_statusNew_ForAllSubtasksWithTheNewStatus();
    }

    @Override
    public void createEpic_statusDone_ForAllSubtasksWithTheDoneStatus() {
        super.createEpic_statusDone_ForAllSubtasksWithTheDoneStatus();
    }

    @Override
    public void createEpic_statusInProgress_ForAllSubtasksWithNewAndDoneStatuses() {
        super.createEpic_statusInProgress_ForAllSubtasksWithNewAndDoneStatuses();
    }

    @Override
    public void createEpic_statusInProgress_ForAllSubtasksWithNewAndDoneAndIiProgressStatuses() {
        super.createEpic_statusInProgress_ForAllSubtasksWithNewAndDoneAndIiProgressStatuses();
    }

    @Override
    public void updateTask_notUpdate_taskIfNull() {
        super.updateTask_notUpdate_taskIfNull();
    }

    @Override
    public void updateEpic_notUpdate_epicIfNull() {
        super.updateEpic_notUpdate_epicIfNull();
    }

    @Override
    public void updateSubtask_notUpdate_subtaskIfNull() {
        super.updateSubtask_notUpdate_subtaskIfNull();
    }

    @Override
    public void deleteAllTask_listOfTaskIsEmpty_ifDeleteAllTheTasks() {
        super.deleteAllTask_listOfTaskIsEmpty_ifDeleteAllTheTasks();
    }

    @Override
    public void deleteAllEpics_listOfEpicIsEmpty_ifDeleteAllTheEpics() {
        super.deleteAllEpics_listOfEpicIsEmpty_ifDeleteAllTheEpics();
    }

    @Override
    public void deleteAllSubtasks_listOfSubtaskIsEmpty_ifDeleteAllTheSubtask() {
        super.deleteAllSubtasks_listOfSubtaskIsEmpty_ifDeleteAllTheSubtask();
    }

    @Override
    public void deleteSubtask_listOfSubtaskIsEmpty_ifDeleteTheSubtaskById() {
        super.deleteSubtask_listOfSubtaskIsEmpty_ifDeleteTheSubtaskById();
    }

    @Override
    public void deleteTask_listOfTaskIsEmpty_ifDeleteTheTaskById() {
        super.deleteTask_listOfTaskIsEmpty_ifDeleteTheTaskById();
    }

    @Override
    public void deleteEpic_listOfEpicIsEmpty_ifDeleteTheEpicById() {
        super.deleteEpic_listOfEpicIsEmpty_ifDeleteTheEpicById();
    }

    @Override
    public void deleteTask_notDeleteTask_IfBadId() {
        super.deleteTask_notDeleteTask_IfBadId();
    }

    @Override
    public void deleteEpic_notDeleteEpic_IfBadId() {
        super.deleteEpic_notDeleteEpic_IfBadId();
    }

    @Override
    public void deleteSubtask_notDeleteSubtask_IfBadId() {
        super.deleteSubtask_notDeleteSubtask_IfBadId();
    }

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
    public void saveAndLoadFromFile_allAddedTasksWillLoadCorrectly_underNormalConditions(){
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
