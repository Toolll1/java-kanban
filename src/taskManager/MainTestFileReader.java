package taskManager;

import taskManager.manager.file.FileBackedTasksManager;
import taskManager.manager.Managers;
import taskManager.task.Status;
import taskManager.task.Subtask;
import taskManager.task.Task;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class MainTestFileReader {

    public static void main(String[] args) {
        File file = Path.of("src", "taskManager", "task", "file.csv").toFile();

        FileBackedTasksManager taskManager = Managers.getDefaultFileBackedTasksManager(file);

        System.out.println("История запросов:");
        List<Task> history = taskManager.getHistory();
        int j = 1;
        for (int i = (history.size() - 1); i >= 0; i--) {
            System.out.println(j + " - " + history.get(i));
            j++;
        }

        System.out.println();
        taskManager.createSubtask(new Subtask(Status.NEW, "Subtask2.1", "описание1", 7));
        taskManager.updateSubtask(4, new Subtask(Status.IN_PROGRESS, "Subtask1.1", "описание1",4, 3));
        taskManager.getSubtask(4);
        System.out.println(taskManager.getSubtasksByEpicId(3));
        System.out.println();
        System.out.println(taskManager.getSubtasksByEpicId(7));
        System.out.println();
        System.out.println(taskManager.getAllEpic());
        System.out.println();
        taskManager.getSubtask(8);
        System.out.println(taskManager.getAllSubask());

    }

}
