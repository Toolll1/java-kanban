package ru.yandex.practicum.Viyunnikov.taskManager.manager.task;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.Viyunnikov.taskManager.task.Epic;
import ru.yandex.practicum.Viyunnikov.taskManager.task.Status;
import ru.yandex.practicum.Viyunnikov.taskManager.task.Subtask;
import ru.yandex.practicum.Viyunnikov.taskManager.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected Task createTask() {
        return new Task(Status.NEW, "Task1", "описание1",
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), Duration.ofMinutes(3 * 25 * 61));
    }

    protected Epic createEpic() {

        return new Epic(Status.NEW, "Epic1", "описание1",
                LocalDateTime.of(2023, 1, 2, 0, 0, 0), Duration.ofMinutes(0));
    }

    protected Subtask createSubtask(Epic epic) {
        return new Subtask(Status.NEW, "Subtask1.1", "описание1", epic.getId(),
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), Duration.ofMinutes(5));
    }


    @Test
    public void getHistory_returnsTheCorrectTaskList_AfterCallingTheMethodGetTask() {

        Task task = createTask();

        manager.createTask(task);
        manager.getTask(1);

        assertEquals(manager.getAllTask(), manager.getHistory());
    }

    @Test
    void createTask_setNewId_existIdInObject() {
        Task task = createTask();
        manager.createTask(task);

        final Task savedTask = manager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task.getId(), savedTask.getId(), "Id задач не совпадают.");
        assertEquals(task.getTitle(), savedTask.getTitle(), "Названия задач не совпадают.");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описания задач не совпадают.");
        assertEquals(task.getDuration(), savedTask.getDuration(), "Продолжительности задач не совпадают.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статусы задач не совпадают.");
        assertEquals(task.getStartTime(), savedTask.getStartTime(), "Время начала задач не совпадает.");
        assertEquals(task.getEndTime(), savedTask.getEndTime(), "Время окончания задач не совпадает.");

        final List<Task> tasks = (List<Task>) manager.getAllTask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createEpic_setNewId_existIdInObject() {
        Epic epic = createEpic();
        manager.createEpic(epic);

        final Epic epic1 = manager.getEpic(epic.getId());

        assertNotNull(epic1, "Эпик не найден.");
        assertEquals(epic.getId(), epic1.getId(), "Id эпиков не совпадают.");
        assertEquals(epic.getTitle(), epic1.getTitle(), "Названия эпиков не совпадают.");
        assertEquals(epic.getDescription(), epic1.getDescription(), "Описания эпиков не совпадают.");
        assertEquals(epic.getDuration(), epic1.getDuration(), "Продолжительности эпиков не совпадают.");
        assertEquals(epic.getStatus(), epic1.getStatus(), "Статусы эпиков не совпадают.");
        assertEquals(epic.getStartTime(), epic1.getStartTime(), "Время начала эпиков не совпадает.");
        assertEquals(epic.getEndTime(), epic1.getEndTime(), "Время окончания эпиков не совпадает.");

        final List<Epic> epics = (List<Epic>) manager.getAllEpic();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void createSubtask_setNewId_existIdInObject() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);

        final Subtask subtask1 = manager.getSubtask(subtask.getId());

        assertNotNull(subtask1, "Подзадача не найдена.");
        assertEquals(subtask.getId(), subtask1.getId(), "Id подзадач не совпадают.");
        assertEquals(subtask.getEpicId(), subtask1.getEpicId(), "Id эпиков подзадач не совпадают.");
        assertEquals(subtask.getTitle(), subtask1.getTitle(), "Названия подзадач не совпадают.");
        assertEquals(subtask.getDescription(), subtask1.getDescription(), "Описания подзадач не совпадают.");
        assertEquals(subtask.getDuration(), subtask1.getDuration(), "Продолжительности подзадач не совпадают.");
        assertEquals(subtask.getStatus(), subtask1.getStatus(), "Статусы подзадач не совпадают.");
        assertEquals(subtask.getStartTime(), subtask1.getStartTime(), "Время начала подзадач не совпадает.");
        assertEquals(subtask.getEndTime(), subtask1.getEndTime(), "Время окончания подзадач не совпадает.");

        final List<Subtask> subtasks = (List<Subtask>) manager.getAllSubtask();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    public void updateTask_statusToInProgress_whenAdjustingTheTaskStatus() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, manager.getTask(task.getId()).getStatus());
    }

    @Test
    public void updateEpic_statusToInProgress_whenAdjustingTheEpicStatus() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void updateSubtask_statusToInProgress_whenAdjustingTheSubtaskStatus() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, manager.getSubtask(subtask.getId()).getStatus());
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void updateTask_statusToInDone_whenAdjustingTheTaskStatus() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(Status.DONE);
        manager.updateTask(task);
        assertEquals(Status.DONE, manager.getTask(task.getId()).getStatus());
    }

    @Test
    public void updateEpic_statusToInDone_whenAdjustingTheEpicStatus() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(Status.DONE);
        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void updateSubtask_statusToInDone_whenAdjustingTheSubtaskStatus() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);
        assertEquals(Status.DONE, manager.getSubtask(subtask.getId()).getStatus());
        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void createEpic_statusNew_withAnEmptyListOfSubtasks() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        assertEquals(Status.NEW, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void createEpic_statusNew_ForAllSubtasksWithTheNewStatus() {
        Epic epic = createEpic();

        manager.createEpic(epic);
        manager.createSubtask(new Subtask(Status.NEW, "Subtask1.1", "описание1", 1,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), Duration.ofMinutes(5)));
        manager.createSubtask(new Subtask(Status.NEW, "Subtask1.2", "описание2", 1,
                LocalDateTime.of(2023, 1, 14, 15, 35, 0), Duration.ofMinutes(3)));
        manager.createSubtask(new Subtask(Status.NEW, "Subtask1.3", "описание3", 1,
                LocalDateTime.of(2023, 1, 14, 15, 40, 0), Duration.ofMinutes(10)));

        assertEquals(Status.NEW, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void createEpic_statusDone_ForAllSubtasksWithTheDoneStatus() {
        Epic epic = createEpic();

        manager.createEpic(epic);
        manager.createSubtask(new Subtask(Status.DONE, "Subtask1.1", "описание1", 1,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), Duration.ofMinutes(5)));
        manager.createSubtask(new Subtask(Status.DONE, "Subtask1.2", "описание2", 1,
                LocalDateTime.of(2023, 1, 14, 15, 35, 0), Duration.ofMinutes(3)));
        manager.createSubtask(new Subtask(Status.DONE, "Subtask1.3", "описание3", 1,
                LocalDateTime.of(2023, 1, 14, 15, 40, 0), Duration.ofMinutes(10)));

        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }


    @Test
    public void createEpic_statusInProgress_ForAllSubtasksWithNewAndDoneStatuses() {
        Epic epic = createEpic();

        manager.createEpic(epic);
        manager.createSubtask(new Subtask(Status.NEW, "Subtask1.1", "описание1", 1,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), Duration.ofMinutes(5)));
        manager.createSubtask(new Subtask(Status.DONE, "Subtask1.2", "описание2", 1,
                LocalDateTime.of(2023, 1, 14, 15, 35, 0), Duration.ofMinutes(3)));
        manager.createSubtask(new Subtask(Status.DONE, "Subtask1.3", "описание3", 1,
                LocalDateTime.of(2023, 1, 14, 15, 40, 0), Duration.ofMinutes(10)));

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void createEpic_statusInProgress_ForAllSubtasksWithNewAndDoneAndIiProgressStatuses() {
        Epic epic = createEpic();

        manager.createEpic(epic);
        manager.createSubtask(new Subtask(Status.NEW, "Subtask1.1", "описание1", 1,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), Duration.ofMinutes(5)));
        manager.createSubtask(new Subtask(Status.DONE, "Subtask1.2", "описание2", 1,
                LocalDateTime.of(2023, 1, 14, 15, 35, 0), Duration.ofMinutes(3)));
        manager.createSubtask(new Subtask(Status.IN_PROGRESS, "Subtask1.3", "описание3", 1,
                LocalDateTime.of(2023, 1, 14, 15, 40, 0), Duration.ofMinutes(10)));

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void updateTask_notUpdate_taskIfNull() {
        Task task = createTask();
        manager.createTask(task);
        manager.updateTask(null);
        Task task1 = manager.getTask(task.getId());

        assertEquals(task.getId(), task1.getId(), "Id задач не совпадают.");
        assertEquals(task.getTitle(), task1.getTitle(), "Названия задач не совпадают.");
        assertEquals(task.getDescription(), task1.getDescription(), "Описания задач не совпадают.");
        assertEquals(task.getDuration(), task1.getDuration(), "Продолжительности задач не совпадают.");
        assertEquals(task.getStatus(), task1.getStatus(), "Статусы задач не совпадают.");
        assertEquals(task.getStartTime(), task1.getStartTime(), "Время начала задач не совпадает.");
        assertEquals(task.getEndTime(), task1.getEndTime(), "Время окончания задач не совпадает.");
    }

    @Test
    public void updateEpic_notUpdate_epicIfNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.updateEpic(null);

        Epic epic1 = manager.getEpic(epic.getId());

        assertEquals(epic.getId(), epic1.getId(), "Id эпиков не совпадают.");
        assertEquals(epic.getTitle(), epic1.getTitle(), "Названия эпиков не совпадают.");
        assertEquals(epic.getDescription(), epic1.getDescription(), "Описания эпиков не совпадают.");
        assertEquals(epic.getDuration(), epic1.getDuration(), "Продолжительности эпиков не совпадают.");
        assertEquals(epic.getStatus(), epic1.getStatus(), "Статусы эпиков не совпадают.");
        assertEquals(epic.getStartTime(), epic1.getStartTime(), "Время начала эпиков не совпадает.");
        assertEquals(epic.getEndTime(), epic1.getEndTime(), "Время окончания эпиков не совпадает.");

    }

    @Test
    public void updateSubtask_notUpdate_subtaskIfNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.updateSubtask(null);
        Subtask subtask1 = manager.getSubtask(subtask.getId());

        assertNotNull(subtask1, "Подзадача не найдена.");
        assertEquals(subtask.getId(), subtask1.getId(), "Id подзадач не совпадают.");
        assertEquals(subtask.getEpicId(), subtask1.getEpicId(), "Id эпиков подзадач не совпадают.");
        assertEquals(subtask.getTitle(), subtask1.getTitle(), "Названия подзадач не совпадают.");
        assertEquals(subtask.getDescription(), subtask1.getDescription(), "Описания подзадач не совпадают.");
        assertEquals(subtask.getDuration(), subtask1.getDuration(), "Продолжительности подзадач не совпадают.");
        assertEquals(subtask.getStatus(), subtask1.getStatus(), "Статусы подзадач не совпадают.");
        assertEquals(subtask.getStartTime(), subtask1.getStartTime(), "Время начала подзадач не совпадает.");
        assertEquals(subtask.getEndTime(), subtask1.getEndTime(), "Время окончания подзадач не совпадает.");
    }

    @Test
    public void deleteAllTask_listOfTaskIsEmpty_ifDeleteAllTheTasks() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteAllTask();
        assertEquals(Collections.EMPTY_LIST, manager.getAllTask());
    }

    @Test
    public void deleteAllEpics_listOfEpicIsEmpty_ifDeleteAllTheEpics() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteAllEpics();
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpic());
    }

    @Test
    public void deleteAllSubtasks_listOfSubtaskIsEmpty_ifDeleteAllTheSubtask() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.deleteAllSubtasks();
        assertTrue(epic.getSubtaskIds().isEmpty());
        assertTrue(manager.getAllSubtask().isEmpty());
    }

    @Test
    public void deleteSubtask_listOfSubtaskIsEmpty_ifDeleteTheSubtaskById() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.deleteSubtask(2);
        assertTrue(epic.getSubtaskIds().isEmpty());
        assertTrue(manager.getAllSubtask().isEmpty());
    }

    @Test
    public void deleteTask_listOfTaskIsEmpty_ifDeleteTheTaskById() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTask(task.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getAllTask());
    }

    @Test
    public void deleteEpic_listOfEpicIsEmpty_ifDeleteTheEpicById() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteEpic(epic.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpic());
    }

    @Test
    public void deleteTask_notDeleteTask_IfBadId() {
        Task task = createTask();
        manager.createTask(task);

        assertEquals(1, manager.getAllTask().size());

        manager.deleteTask(999);
        assertFalse(false, String.valueOf(manager.getAllTask().contains(manager.getTask(999))));

        assertEquals(List.of(task), manager.getAllTask());
        assertEquals(1, manager.getAllTask().size());
    }

    @Test
    public void deleteEpic_notDeleteEpic_IfBadId() {
        Epic epic = createEpic();
        manager.createEpic(epic);

        assertEquals(1, manager.getAllEpic().size());

        manager.deleteEpic(999);
        assertFalse(false, String.valueOf(manager.getAllEpic().contains(manager.getEpic(999))));

        assertEquals(List.of(epic), manager.getAllEpic());

        assertEquals(1, manager.getAllEpic().size());
    }

    @Test
    public void deleteSubtask_notDeleteSubtask_IfBadId() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);

        assertEquals(1, manager.getAllSubtask().size());

        manager.deleteSubtask(999);
        assertFalse(false, String.valueOf(manager.getAllSubtask().contains(manager.getSubtask(999))));

        assertEquals(List.of(subtask), manager.getAllSubtask());
        assertEquals(1, manager.getAllSubtask().size());

        assertEquals(List.of(subtask.getId()), manager.getEpic(epic.getId()).getSubtaskIds());
    }
}