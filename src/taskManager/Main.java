package taskManager;

import taskManager.manager.TaskManager;
import taskManager.manager.Managers;
import taskManager.status.Status;
import taskManager.task.Epic;
import taskManager.task.Subtask;
import taskManager.task.Task;

import java.util.List;


public class Main {

        public static void main(String[] args) {
        TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());

        taskManager.createTask(new Task(Status.NEW, "Task1", "описание"));
        taskManager.createTask(new Task(Status.NEW, "Task2", "описание2"));
        taskManager.createEpic(new Epic(Status.NEW, "Epic1", "описание"));
        taskManager.createEpic(new Epic(Status.NEW, "Epic2", "описание2"));
        taskManager.createSubtask(new Subtask(Status.NEW, "Subtask1", "описание", 3));
        taskManager.createSubtask(new Subtask(Status.NEW, "Subtask1.2", "описание2", 3 ));
        taskManager.createSubtask(new Subtask(Status.NEW, "Subtask2", "описание", 4));

        System.out.println("Добавили всё");
        System.out.println(taskManager.getAllTask());
        System.out.println();
        System.out.println(taskManager.getAllEpic());
        System.out.println();
        System.out.println(taskManager.getAllSubask());

        taskManager.updateTask(new Task(Status.IN_PROGRESS, "Task1", "описание", 1));
        taskManager.updateTask(new Task(Status.DONE, "Task2", "описание2", 2));
        taskManager.updateSubtask(5, new Subtask(Status.DONE, "Subtask1", "описание", 3));
        taskManager.updateSubtask(6, new Subtask(Status.IN_PROGRESS, "Subtask1.2", "описание2", 3));
        //taskManager.deleteAllSubtasks();
        taskManager.updateSubtask(7, new Subtask(Status.DONE, "Subtask2", "описание", 4));

        System.out.println();
        System.out.println();
        System.out.println("Изменили всё");
        System.out.println(taskManager.getAllTask());
        System.out.println();
        System.out.println(taskManager.getAllEpic());
        System.out.println();
        System.out.println(taskManager.getAllSubask());

        taskManager.deleteTask(1);
        taskManager.deleteEpic(3);

        System.out.println();
        System.out.println();
        System.out.println("удалили таск 1 и епик 3");
        System.out.println(taskManager.getAllTask());
        System.out.println();
        System.out.println(taskManager.getAllEpic());
        System.out.println();
        System.out.println(taskManager.getAllSubask());

        taskManager.getTask(2);
        taskManager.getTask(2);
        taskManager.getEpic(4);
        taskManager.getSubtask(7);
        taskManager.getSubtask(7);
        taskManager.getEpic(4);
        taskManager.getSubtask(7);
        taskManager.getSubtask(7);
        taskManager.getEpic(4);
        taskManager.getSubtask(7);
        taskManager.getSubtask(7);
        taskManager.getEpic(4);
        taskManager.getSubtask(7);
        taskManager.getSubtask(7);
        taskManager.getEpic(4);
        taskManager.getSubtask(7);
        taskManager.getSubtask(7);
        List<Task> history = taskManager.getHistory();

        System.out.println();
        System.out.println();
        System.out.println("Последние 10 запросов в гет:");
        System.out.println(history);
        }
}
