package ru.yandex.practicum.vyunnikov.taskManager.managers.history;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vyunnikov.taskManager.managers.Managers;
import ru.yandex.practicum.vyunnikov.taskManager.task.Status;
import ru.yandex.practicum.vyunnikov.taskManager.task.Task;

import java.time.LocalDateTime;
import java.util.Collections;

import static java.time.Duration.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class InMemoryHistoryManagerTest {
    HistoryManager historyManager = Managers.getHistory();

    @Test
    public void remove_theTaskWithTheCorrectIdWillBeDeleted_ifRemoveTaskAndNonExistentTask() {

        historyManager.add(new Task(Status.NEW, "Task1", "описание1", 1,
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000)));
        historyManager.add(new Task(Status.NEW, "Task2", "описание2", 2,
                LocalDateTime.of(2023, 1, 14, 15, 15, 0), ofMinutes(15)));
        historyManager.add(new Task(Status.NEW, "Task2", "описание2", 3,
                LocalDateTime.of(2023, 1, 15, 21, 0, 0), ofMinutes(15)));

        assertEquals(3,historyManager.getHistory().size());

        historyManager.remove(1);
        historyManager.remove(999);

        assertFalse(historyManager.getHistory().contains(new Task(Status.NEW, "Task1", "описание1", 1,
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000))));
        assertEquals(2,historyManager.getHistory().size());
    }

    @Test
    public void remove_emptyHistory_whenDeletingAllTasks() {
        historyManager.add(new Task(Status.NEW, "Task1", "описание1", 1,
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000)));
        historyManager.remove(1);

        assertEquals(Collections.EMPTY_LIST, historyManager.getHistory());
    }

    @Test
    public void add_theDuplicateWillNotBeAdded_whenTryingToAddADuplicate() {
        Task task = new Task(Status.NEW, "Task1", "описание1", 1,
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000));

        historyManager.add(task);
        historyManager.add(task);
        Task task1 = historyManager.getHistory().get(0);

        assertEquals(1, historyManager.getHistory().size());

        assertEquals(task.getId(), task1.getId(), "Id задач не совпадают.");
        assertEquals(task.getTitle(), task1.getTitle(), "Названия задач не совпадают.");
        assertEquals(task.getDescription(), task1.getDescription(), "Описания задач не совпадают.");
        assertEquals(task.getDuration(), task1.getDuration(), "Продолжительности задач не совпадают.");
        assertEquals(task.getStatus(), task1.getStatus(), "Статусы задач не совпадают.");
        assertEquals(task.getStartTime(), task1.getStartTime(), "Время начала задач не совпадает.");
        assertEquals(task.getEndTime(), task1.getEndTime(), "Время окончания задач не совпадает.");
    }
}
