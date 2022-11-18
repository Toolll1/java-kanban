package taskManager.manager;

import taskManager.task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_SIZE = 10;
    private final List<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (history.size() >= MAX_SIZE) {
            history.remove(0);
            history.add(task);
        } else {
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
