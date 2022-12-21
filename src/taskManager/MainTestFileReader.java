package taskManager;

import taskManager.manager.file.FileBackedTasksManager;
import taskManager.manager.Managers;
import taskManager.task.Task;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class MainTestFileReader {

    public static void main(String[] args) {
        File file = Path.of("src", "taskManager", "task", "file.csv").toFile();
        FileBackedTasksManager taskManager = Managers.getDefaultFileBackedTasksManager(file.toString());

        taskManager.loadFromFile(file);

        System.out.println();
        System.out.println();
        System.out.println("История запросов:");
        List<Task> history = taskManager.getHistory();
        int j = 1;
        for (int i = (history.size() - 1); i >= 0; i--) {
            System.out.println(j + " - " + history.get(i));
            j++;
        }
    }
}
