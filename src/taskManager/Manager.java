package taskManager;

import taskManager.task.Epic;
import taskManager.task.status.Status;
import taskManager.task.Subtask;
import taskManager.task.Task;

import java.util.*;

public class Manager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int i = 0;

    private int newId() {
        return ++i;
    }

    void createTask(Task task) {
        task.setId(newId());
        tasks.put(task.getId(), task);
    }

    void updateTask(Task task) {

        if (tasks.containsKey(task.getId())) {
            task.setId(task.getId());
            tasks.replace(task.getId(), task);
        } else {
            System.out.println("нет такого таска");
        }
    }

    Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    void deleteTask(int taskId) {
        tasks.remove(taskId);
    }


    public void createEpic(Epic epic) {
        epic.setId(newId());
        epics.put(epic.getId(), epic);
    }

    void updateEpic(Epic epic) {
        epic.setId(epic.getId());
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("нет такого эпика");
        }
    }

    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    void deleteEpic(int epicId) {
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

    void updateSubtask(int subtaskId, Subtask subtask) {
        subtask.setId(subtaskId);
        if (subtasks.containsKey(subtaskId)) {
            subtasks.replace(subtaskId, subtask);
            Integer x = subtask.getEpicId();
            this.updateEpicStatus(epics.get(x));
        } else {
            System.out.println("нет такого сабтаска");
        }
    }

    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        List<Integer> subtasksFromEpic = epic.getSubtaskIds();
        for (Integer x : subtasksFromEpic) {
            listOfSubtasks.add(subtasks.get(x));
        }
        return listOfSubtasks;
    }

    void deleteSubtask(int subtaskId) {
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

    public Collection<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    public Collection<Subtask> getAllSubask() {
        return new ArrayList<>(subtasks.values());
    }

    public Collection<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    void deleteAllTask() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }
    }

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
