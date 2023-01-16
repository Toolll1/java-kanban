package task;

import org.junit.jupiter.api.BeforeEach;
import taskManager.manager.task.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
    }
}
