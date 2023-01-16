package taskManager;

import taskManager.manager.task.TaskManager;
import taskManager.manager.Managers;
import taskManager.task.Status;
import taskManager.task.Epic;
import taskManager.task.Subtask;
import taskManager.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.Duration.*;

public class MainTestHystory {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultTask();

        taskManager.createTask(new Task(Status.NEW, "Task1", "описание1",
                LocalDateTime.of(2023, 5, 1, 1, 0, 0), ofMinutes(5000)));
        taskManager.createTask(new Task(Status.NEW, "Task2", "описание2",
                LocalDateTime.of(2023, 1, 14, 15, 15, 0), ofMinutes(15)));

        taskManager.createEpic(new Epic(Status.NEW, "Epic1", "описание1",
                LocalDateTime.of(2023, 1, 2, 0, 0, 0), ofMinutes(0)));
        taskManager.createSubtask(new Subtask(Status.NEW, "Subtask1.1", "описание1", 3,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5)));
        taskManager.createSubtask(new Subtask(Status.NEW, "Subtask1.2", "описание2", 3,
                LocalDateTime.of(2023, 1, 14, 15, 35, 0), ofMinutes(3)));
        taskManager.createSubtask(new Subtask(Status.NEW, "Subtask1.3", "описание3", 3,
                LocalDateTime.of(2023, 1, 14, 15, 40, 0), ofMinutes(10)));

        taskManager.createEpic(new Epic(Status.NEW, "Epic2", "описание2",
                LocalDateTime.of(2023, 3, 2, 0, 1, 1), ofMinutes(60*24*365)));

        taskManager.updateSubtask(new Subtask(Status.IN_PROGRESS, "Subtask1.1", "описание1", 4,3,
                LocalDateTime.of(2023, 1, 14, 15, 30, 0), ofMinutes(5)));

        System.out.println("Добавили всё");
        System.out.println(taskManager.getAllTask());
        System.out.println();
        System.out.println(taskManager.getAllEpic());
        System.out.println();
        System.out.println(taskManager.getAllSubtask());

        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(3);
        taskManager.getSubtask(4);
        taskManager.getSubtask(5);
        taskManager.getSubtask(6);
        taskManager.getEpic(7);
        taskManager.getSubtask(6);
        taskManager.getEpic(3);
        taskManager.getEpic(7);
        taskManager.getSubtask(5);
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getSubtask(4);


        System.out.println();
        System.out.println();
        System.out.println("История запросов:");
        List<Task> history = taskManager.getHistory();
        int n = 1;
        for (int i = (history.size() - 1); i >= 0; i--) {
            System.out.println(n + " - " + history.get(i));
            n++;
        }

        List<Task> tasks = taskManager.getPrioritizedTasks();

        System.out.println();
        System.out.println("А теперь в порядке приоритета");
        System.out.println();
        for (Task task : tasks) {
            System.out.println(task);
        }

    }
}
