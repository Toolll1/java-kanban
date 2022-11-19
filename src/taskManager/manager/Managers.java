package taskManager.manager;

import taskManager.manager.managerForHistory.HistoryManager;
import taskManager.manager.managerForHistory.InMemoryHistoryManager;
import taskManager.manager.managerForTask.InMemoryTaskManager;
import taskManager.manager.managerForTask.TaskManager;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTask() {
        return new InMemoryTaskManager();
    }
}
