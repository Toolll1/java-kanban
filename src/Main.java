import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        manager.createTask(new Task(Status.NEW, "Task1", "описание"));
        manager.createTask(new Task(Status.NEW, "Task2", "описание2"));
        manager.createEpic(new Epic(Status.NEW, "Epic1", "описание"));
        manager.createEpic(new Epic(Status.NEW, "Epic2", "описание2"));
        manager.createSubtask(new Subtask(Status.NEW, "Subtask1", "описание", 3));
        manager.createSubtask(new Subtask(Status.NEW, "Subtask1.2", "описание2", 3 ));
        manager.createSubtask(new Subtask(Status.NEW, "Subtask2", "описание", 4));

        System.out.println(manager.getAllTask());
        System.out.println();
        System.out.println(manager.getAllEpic());
        System.out.println();
        System.out.println(manager.getAllSubask());

        manager.updateTask(1, new Task(Status.IN_PROGRESS, "Task1", "описание"));
        manager.updateTask(2, new Task(Status.DONE, "Task2", "описание2"));
        manager.updateSubtask(5, new Subtask(Status.DONE, "Subtask1", "описание", 3));
        manager.updateSubtask(6, new Subtask(Status.IN_PROGRESS, "Subtask1.2", "описание2", 3));
        manager.updateSubtask(7, new Subtask(Status.DONE, "Subtask2", "описание", 4));

        System.out.println();
        System.out.println();
        System.out.println(manager.getAllTask());
        System.out.println();
        System.out.println(manager.getAllEpic());
        System.out.println();
        System.out.println(manager.getAllSubask());

        manager.deleteTask(1);
        manager.deleteEpic(3);

        System.out.println();
        System.out.println();
        System.out.println(manager.getAllTask());
        System.out.println();
        System.out.println(manager.getAllEpic());
        System.out.println();
        System.out.println(manager.getAllSubask());
    }
}
