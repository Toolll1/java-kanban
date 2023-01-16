import org.junit.jupiter.api.Test;
import taskManager.manager.Managers;
import taskManager.manager.history.HistoryManager;
import taskManager.manager.task.TaskManager;
import taskManager.task.Status;
import taskManager.task.Task;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.time.Duration.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    TaskManager manager = Managers.getDefaultTask();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    public void addTasksToHistory() {
        createTasks();
        List<Task> tasks = createListTasks();
        for (Task task : tasks) {
            historyManager.add(task);
        }
        assertEquals(manager.getAllTask(), historyManager.getHistory());
    }

    @Test
    public void removeTask() {
        createTasks();
        List<Task> tasks = new ArrayList<>(createListTasks());
        for (Task task : tasks) {
            historyManager.add(task);
        }

        tasks.remove(manager.getTask(1));
        historyManager.remove(1);

        assertEquals(tasks, historyManager.getHistory());
    }

    @Test
    public void emptyHistory() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void duplication() {
        Task task = new Task(Status.NEW, "Task1", "описание1", 1,
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(3 * 25 * 61));

        historyManager.add(task);
        historyManager.add(task);
        List<Task> tasks = List.of(task);

        assertEquals(tasks, historyManager.getHistory());
    }

    public void createTasks() {
        manager.createTask(new Task(Status.NEW, "Task1", "описание1",
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(3 * 25 * 61)));
        manager.createTask(new Task(Status.NEW, "Task2", "описание2",
                LocalDateTime.of(2023, 1, 14, 15, 15, 0), ofMinutes(15)));
        manager.createTask(new Task(Status.NEW, "Task2", "описание2",
                LocalDateTime.of(2023, 1, 15, 21, 0, 0), ofMinutes(15)));
    }

    public List<Task> createListTasks() {
        return List.of(
                new Task(Status.NEW, "Task1", "описание1", 1,
                        LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(3 * 25 * 61)),
                new Task(Status.NEW, "Task2", "описание2", 2,
                        LocalDateTime.of(2023, 1, 14, 15, 15, 0), ofMinutes(15)),
                new Task(Status.NEW, "Task2", "описание2", 3,
                        LocalDateTime.of(2023, 1, 15, 21, 0, 0), ofMinutes(15))
        );
    }
}
