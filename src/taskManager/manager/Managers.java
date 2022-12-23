package taskManager.manager;

import taskManager.manager.file.FileBackedTasksManager;
import taskManager.manager.history.HistoryManager;
import taskManager.manager.history.InMemoryHistoryManager;
import taskManager.manager.task.InMemoryTaskManager;
import taskManager.manager.task.TaskManager;

import java.io.File;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTask() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager getDefaultFileBackedTasksManager() {
        return FileBackedTasksManager.getTaskManagerForFile();
    }
    public static FileBackedTasksManager getDefaultFileBackedTasksManager(File file) {
        return FileBackedTasksManager.getTaskManagerForFile(file);
    }

}