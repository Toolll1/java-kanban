package taskManager.manager.task;

import taskManager.exceptions.ManagerValidateException;
import taskManager.manager.history.HistoryManager;
import taskManager.manager.Managers;
import taskManager.task.Epic;
import taskManager.task.Status;
import taskManager.task.Subtask;
import taskManager.task.Task;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks = new HashMap<>(); //+
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
    protected Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    private int id = 0;

    public void setId(int id) {
        this.id = id;
    }

    protected int newId() {
        return ++id;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void createTask(Task task) {
        task.setId(newId());
        addNewPrioritizedTask(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            addNewPrioritizedTask(task);
            tasks.replace(task.getId(), task);
        } else {
            System.out.println("нет такого таска");
        }
    }

    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
            return task;
        } else {
            System.out.println("нет такого таска");
            return null;
        }
    }

    @Override
    public void deleteTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            prioritizedTasks.removeIf(task -> task.getId() == taskId);
            tasks.remove(taskId);
            historyManager.remove(taskId);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(newId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
            updateEpicTime(epic);
        } else {
            System.out.println("нет такого эпика");
        }
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        } else {
            System.out.println("нет такого эпика");
            return null;
        }
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epics.remove(epicId);
            historyManager.remove(epicId);
        } else {
            System.out.println("нет такого эпика");
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask.getEpicId() != null) {
            int subtaskId = this.newId();
            subtask.setId(subtaskId);
            subtasks.put(subtaskId, subtask);
            int epicIdOfSubtask = subtask.getEpicId();
            Epic epic = epics.get(epicIdOfSubtask);
            if (epic != null) {
                addNewPrioritizedTask(subtask);
                epic.addSubTaskId(subtaskId);
                updateEpicStatus(epics.get(subtask.getEpicId()));
                updateEpicTime(epic);
            }
        } else {
            System.out.println("ошибка: у сабтаска нет id эпика");
        }
    }


    @Override
    public void updateSubtask(int subtaskId, Subtask subtask) {
        if (subtasks.containsKey(subtaskId)) {
            addNewPrioritizedTask(subtask);
            subtasks.replace(subtaskId, subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
        } else {
            System.out.println("нет такого сабтаска");
        }
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        } else {
            System.out.println("нет такого субтаска");
            return null;
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        List<Integer> subtasksFromEpic = epic.getSubtaskIds();
        for (Integer x : subtasksFromEpic) {
            listOfSubtasks.add(subtasks.get(x));
        }
        return listOfSubtasks;
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        int epicIdOfSubTask = subtasks.get(subtaskId).getEpicId();
        Epic epic = epics.get(epicIdOfSubTask);
        subtasks.remove(subtaskId);
        historyManager.remove(subtaskId);
        List<Integer> subtasksFromEpic = epic.getSubtaskIds();
        for (int i = 0; i < subtasksFromEpic.size(); i++) {
            if (subtasksFromEpic.get(i) == subtaskId) {
                epic.removeSubTaskId(i);
            }
        }
        updateEpicStatus(epic);
        updateEpicTime(epic);
        prioritizedTasks.remove(subtask);
    }

    @Override
    public Collection<Task> getAllTask() {
        if (tasks.size() == 0) {
            System.out.println("Список задач пуст");
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Collection<Subtask> getAllSubask() {
        if (subtasks.size() == 0) {
            System.out.println("Список подзадач пуст");
            return Collections.emptyList();
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Collection<Epic> getAllEpic() {
        if (epics.size() == 0) {
            System.out.println("Список эпиков пуст");
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                prioritizedTasks.remove(subtask);
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
            ArrayList<Integer> x = epic.getSubtaskIds();
            listOfSubtasks.add(subtasks.get(x.get(i)));
        }

        int statusDone = 0;
        int statusNew = 0;

        for (Subtask subtask : listOfSubtasks) {
            switch (subtask.getStatus()) {
                case NEW -> statusNew++;
                case DONE -> statusDone++;
            }
        }

        if (listOfSubtasks.size() == 0 || statusNew == listOfSubtasks.size()) {
            epic.setStatus(Status.NEW);
        } else if (statusDone == listOfSubtasks.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void validateTaskPriority() {
        List<Task> tasks = getPrioritizedTasks();


        if (tasks.size() > 0) {
            for (int i = 1; i < tasks.size(); i++) {

                Task task1 = tasks.get(i);

                for (int j = tasks.size() - 1; j > i; j--) {
                    Task task2 = tasks.get(j);
                    if (task2.getStartTime().isBefore(task1.getEndTime())) {
                        throw new ManagerValidateException(
                                "Задачи\n" + task1 + "\nи\n" + task2 + "\nпересекаются пересекаются по времени");
                    }
                }
            }

        }
    }

    private void addNewPrioritizedTask(Task task) {
        prioritizedTasks.add(task);
        validateTaskPriority();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void updateEpicTime(Epic epic) {
        List<Subtask> subtasks = getSubtasksByEpicId(epic.getId());
        LocalDateTime startTime = subtasks.get(0).getStartTime();
        LocalDateTime endTime = subtasks.get(0).getEndTime();

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        Duration duration = Duration.between(startTime, endTime);
        epic.setDuration((int) duration.toMinutes());
    }
}
