package taskManager;

import taskManager.manager.File.FileBackedTasksManager;
import taskManager.manager.Managers;
import taskManager.task.Task;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class MainTestFileReader {

    public static void main(String[] args) {
        FileBackedTasksManager taskManager = Managers.getDefaultFileBackedTasksManager();
        File file = Path.of("file.csv").toFile();

        taskManager.loadFromFile(file);

        taskManager.deleteTask(1);
        taskManager.deleteEpic(3);

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
