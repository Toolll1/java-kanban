package taskManager.manager;

import taskManager.manager.File.FileBackedTasksManager;
import taskManager.manager.history.HistoryManager;
import taskManager.manager.history.InMemoryHistoryManager;
import taskManager.manager.task.InMemoryTaskManager;
import taskManager.manager.task.TaskManager;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTask() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager getDefaultFileBackedTasksManager() {
        return new FileBackedTasksManager();
    }

}
