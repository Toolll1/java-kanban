package taskManager.manager;

import taskManager.task.Epic;
import taskManager.status.Status;
import taskManager.task.Subtask;
import taskManager.task.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager{

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int i = 0;

    private int newId() {
        return ++i;
    }

    @Override
    public void printHistory(){
        List<Task> history = getHistory();
        int n = 1;
        for (int i=(history.size()-1); i >= 0; i--){
            System.out.println(n + " - " + history.get(i));
            n++;
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void createTask(Task task) {
        task.setId(newId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.replace(task.getId(), task);
        } else {
            System.out.println("нет такого таска");
        }
    }

    @Override
    public Task getTask(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
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
        } else {
            System.out.println("нет такого эпика");
        }
    }

    @Override
    public Epic getEpic(int epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(epicId);
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
                epic.addSubTaskId(subtaskId);
                this.updateEpicStatus(epics.get(subtask.getEpicId()));
            }
        } else {
            System.out.println("ошибка: у сабтаска нет id эпика");
        }
    }

    @Override
    public void updateSubtask(int subtaskId, Subtask subtask) {
        if (subtasks.containsKey(subtaskId)) {
            subtasks.replace(subtask.getId(), subtask);
            this.updateEpicStatus(epics.get(subtask.getEpicId()));
        } else {
            System.out.println("нет такого сабтаска");
        }
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        historyManager.add(subtasks.get(subtaskId));
        return subtasks.get(subtaskId);
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
        int epicIdOfSubTask = subtasks.get(subtaskId).getEpicId();
        Epic epic = epics.get(epicIdOfSubTask);
        subtasks.remove(subtaskId);
        List<Integer> subtasksFromEpic = epic.getSubtaskIds();
        for (int i = 0; i < subtasksFromEpic.size(); i++) {
            if (subtasksFromEpic.get(i) == subtaskId) {
                epic.removeSubTaskId(i);
            }
        }
        this.updateEpicStatus(epic);
    }

    @Override
    public Collection<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Collection<Subtask> getAllSubask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Collection<Epic> getAllEpic() {
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
                case NEW:
                    statusNew++;
                    break;
                case IN_PROGRESS:
                    break;
                case DONE:
                    statusDone++;
                    break;
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
}
