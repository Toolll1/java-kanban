import org.junit.jupiter.api.Test;
import taskManager.manager.task.TaskManager;
import taskManager.task.Epic;
import taskManager.task.Status;
import taskManager.task.Subtask;
import taskManager.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
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
        return new Subtask(Status.NEW, "Subtask1.1", "описание1", 1,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), Duration.ofMinutes(5));
    }

    @Test
    void addNewTask() {
        Task task = createTask();
        manager.createTask(task);

        final Task savedTask = manager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = (List<Task>) manager.getAllTask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = createEpic();
        manager.createEpic(epic);

        final Epic savedEpic = manager.getEpic(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = (List<Epic>) manager.getAllEpic();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);

        final Subtask savedTask = manager.getSubtask(subtask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");

        final List<Subtask> subtasks = (List<Subtask>) manager.getAllSubtask();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void updateTaskStatusToInProgress() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, manager.getTask(task.getId()).getStatus());
    }

    @Test
    public void updateEpicStatusToInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void updateSubtaskStatusToInProgress() {
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
    public void updateTaskStatusToInDone() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(Status.DONE);
        manager.updateTask(task);
        assertEquals(Status.DONE, manager.getTask(task.getId()).getStatus());
    }

    @Test
    public void updateEpicStatusToInDone() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(Status.DONE);
        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void updateSubtaskStatusToInDone() {
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
    public void calculationOfTheEpicStatusWithAnEmptyListOfSubtasks () {
        Epic epic = createEpic();
        manager.createEpic(epic);
        assertEquals(Status.NEW, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void calculationOfTheEpicStatusForAllSubtasksWithTheNewStatus () {
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
    public void calculationOfTheEpicStatusForAllSubtasksWithTheDoneStatus () {
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
    public void calculationOfTheEpicStatusForAllSubtasksWithNewAndDoneStatuses () {
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
    public void calculationOfTheEpicStatusForAllSubtasksWithNewAndDoneAndIiProgressStatuses () {
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
    public void notUpdateTaskIfNull() {
        Task task = createTask();
        manager.createTask(task);
        manager.updateTask(null);
        assertEquals(task, manager.getTask(task.getId()));
    }

    @Test
    public void notUpdateEpicIfNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.updateEpic(null);
        assertEquals(epic, manager.getEpic(epic.getId()));
    }

    @Test
    public void notUpdateSubtaskIfNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.updateSubtask(null);
        assertEquals(subtask, manager.getSubtask(subtask.getId()));
    }

    @Test
    public void deleteAllTasks() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteAllTask();
        assertEquals(Collections.EMPTY_LIST, manager.getAllTask());
    }

    @Test
    public void deleteAllEpics() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteAllEpics();
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpic());
    }

    @Test
    public void deleteAllSubtasks() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.deleteAllSubtasks();
        assertTrue(epic.getSubtaskIds().isEmpty());
        assertTrue(manager.getAllSubtask().isEmpty());
    }

    @Test
    public void deleteSubtasks() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.deleteSubtask(2);
        assertTrue(epic.getSubtaskIds().isEmpty());
        assertTrue(manager.getAllSubtask().isEmpty());
    }

    @Test
    public void deleteTask() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTask(task.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getAllTask());
    }

    @Test
    public void deleteEpic() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteEpic(epic.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpic());
    }

    @Test
    public void notDeleteTaskIfBadId() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTask(999);
        assertEquals(List.of(task), manager.getAllTask());
    }

    @Test
    public void notDeleteEpicIfBadId() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteEpic(999);
        assertEquals(List.of(epic), manager.getAllEpic());
    }

    @Test
    public void notDeleteSubtaskIfBadId() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.deleteSubtask(999);
        assertEquals(List.of(subtask), manager.getAllSubtask());
        assertEquals(List.of(subtask.getId()), manager.getEpic(epic.getId()).getSubtaskIds());
    }


}