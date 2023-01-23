package ru.yandex.practicum.vyunnikov.taskManager.manager.task;

import ru.yandex.practicum.vyunnikov.taskManager.manager.history.HistoryManager;
import ru.yandex.practicum.vyunnikov.taskManager.manager.Managers;
import ru.yandex.practicum.vyunnikov.taskManager.task.Epic;
import ru.yandex.practicum.vyunnikov.taskManager.task.Status;
import ru.yandex.practicum.vyunnikov.taskManager.task.Subtask;
import ru.yandex.practicum.vyunnikov.taskManager.task.Task;


import java.time.Duration;
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
        if (validateTaskPriority(task)) {
            task.setId(newId());
            addNewPrioritizedTask(task);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
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
        } else {
            System.out.println("нет такого таска");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(newId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatusAndTime(epic);
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
        int epicIdOfSubtask = subtask.getEpicId();
        Epic epic = epics.get(epicIdOfSubtask);

        if (!validateTaskPriority(subtask)) {
            return;
        }
        if (subtask.getEpicId() == null) {
            System.out.println("ошибка: у сабтаска нет id эпика");
            return;
        }
        if (epic == null) {
            System.out.println("ошибка: эпик пустой");
            return;
        }

        int subtaskId = this.newId();
        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);
        addNewPrioritizedTask(subtask);

        epic.addSubTaskId(subtaskId);
        updateEpicStatusAndTime(epic);
    }

    private void updateEpicStatusAndTime(Epic epic) {
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            addNewPrioritizedTask(subtask);
            subtasks.replace(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateEpicStatusAndTime(epic);
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
        if (subtasks.containsKey(subtaskId)) {
            Subtask subtask = subtasks.get(subtaskId);
            int epicIdOfSubtask = subtasks.get(subtaskId).getEpicId();
            Epic epic = epics.get(epicIdOfSubtask);
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            List<Integer> subtasksFromEpic = epic.getSubtaskIds();
            for (int i = 0; i < subtasksFromEpic.size(); i++) {
                if (subtasksFromEpic.get(i) == subtaskId) {
                    epic.removeSubTaskId(i);
                }
            }
            updateEpicStatusAndTime(epic);
            prioritizedTasks.remove(subtask);
        } else {
            System.out.println("нет такого сабтаска");
        }
    }

    @Override
    public Collection<Task> getAllTask() {
        if (tasks.size() == 0) {
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Collection<Subtask> getAllSubtask() {
        if (subtasks.size() == 0) {
            return Collections.emptyList();
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Collection<Epic> getAllEpic() {
        if (epics.size() == 0) {
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTask() {
        for (Task value : tasks.values()) {
            prioritizedTasks.remove(value);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {

        for (Epic epic : epics.values()) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                prioritizedTasks.remove(subtask);
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            subtasks.clear();
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

    protected boolean validateTaskPriority(Task taskToValidate) {
        List<Task> tasks = getPrioritizedTasks();

        if (tasks.size() == 0) return true;

        for (Task task2 : tasks) {
            if ((taskToValidate.getStartTime().isBefore(task2.getEndTime())
                    && taskToValidate.getStartTime().isAfter(task2.getStartTime()))
                    || (taskToValidate.getEndTime().isBefore(task2.getEndTime())
                    && taskToValidate.getEndTime().isAfter(task2.getStartTime()))
                    || taskToValidate.getStartTime().equals(task2.getStartTime())) {
                System.out.println("Задачи\n" + taskToValidate + "\nи\n" + task2 + "\nпересекаются пересекаются по времени");
                return false;
            }
        }

        return true;
    }

    protected void addNewPrioritizedTask(Task task) {
        if (prioritizedTasks.contains(task)) prioritizedTasks.remove(task);
        if (validateTaskPriority(task)) prioritizedTasks.add(task);

    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void updateEpicTime(Epic epic) {
        List<Subtask> subtasks = getSubtasksByEpicId(epic.getId());
        subtasks.sort(taskComparator);

        if (subtasks.size() > 0) {
            LocalDateTime startTime = subtasks.get(0).getStartTime();
            Duration duration = Duration.ofMinutes(0);
            for (Subtask subtask : subtasks) {
                duration = duration.plus(subtask.getDuration());
            }

            epic.setStartTime(startTime);
            epic.setEndTime(startTime.plusMinutes(duration.toMinutes()));
            epic.setDuration(duration);
        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
        }
    }
}
