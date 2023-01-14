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
        System.out.println(taskManager.getSubtasksByEpicId(3));
        System.out.println();
        System.out.println(taskManager.getAllEpic());
        System.out.println();
        System.out.println(taskManager.getAllSubask());

        List<Task> tasks = taskManager.getPrioritizedTasks();

        System.out.println();
        System.out.println("А теперь в порядке приоритета");
        System.out.println();
        for (Task task : tasks) {
            System.out.println(task);
        }

    }
}
