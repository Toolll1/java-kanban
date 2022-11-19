package taskManager.manager.managerForHistory;

import taskManager.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

}