package taskManager.manager.File;

import taskManager.manager.task.InMemoryTaskManager;
import taskManager.task.Epic;
import taskManager.task.Status;
import taskManager.task.Subtask;
import taskManager.task.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/*
* Хочу прочитать твои комментарии перед тем, как улучшать код.
* Того ли вообще от нас требовали?
* 1 раз на запись и один раз на считывание - работает безупречно))
* При считывании все равно вызывается метод save и повторно записывается история.. А если делать через super,
* вся консоль забивается фразой "нет такого таска" (субтаска или эпика), что мне не нравится)))
* */

public class FileBackedTasksManager extends InMemoryTaskManager {
    boolean testGet = true;
    boolean testHeading = true;

    public void save(Object object) {

        if (object instanceof Task) {
            if (testHeading) {
                testHeading = false;
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("file.csv", true))) {
                    writer.write("id, тип, название, статус, описание, EpicId");
                    writer.newLine();
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("file.csv", true))) {
                int id = (((Task) object).getId());
                String title = (((Task) object).getTitle());
                Status status = (((Task) object).getStatus());
                String description = (((Task) object).getDescription());

                if (object instanceof Subtask) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s", id, TaskType.SUBTASK, title,
                            status, description, ((Subtask) object).getEpicId()));
                } else if (object instanceof Epic) {
                    writer.write(String.format("%s,%s,%s,%s,%s", id, TaskType.EPIC, title, status, description));
                } else {
                    writer.write(String.format("%s,%s,%s,%s,%s", id, TaskType.TASK, title, status, description));
                }

                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (object instanceof Integer) {
            if (testGet) {
                testGet = false;
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("file.csv", true))) {
                    writer.newLine();
                    writer.write((int) object + "");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("file.csv", true))) {
                    writer.write("," + (int) object);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void loadFromFile(File file) {

        List<String> list = new ArrayList<>();

        try (Reader fileReader = new FileReader(file); BufferedReader br = new BufferedReader(fileReader)) {
            while (br.ready()) {
                String line = br.readLine();
                list.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 1; i < list.size() - 2; i++) {
            String[] separator = list.get(i).split(",");
            if (Objects.equals(separator[1], "TASK")) {
                super.createTask(new Task(Status.valueOf(separator[3]), separator[2], separator[4]));
            } else if (Objects.equals(separator[1], "EPIC")) {
                super.createEpic(new Epic(Status.valueOf(separator[3]), separator[2], separator[4]));
            } else if (Objects.equals(separator[1], "SUBTASK")) {
                super.createSubtask(new Subtask(Status.valueOf(separator[3]), separator[2], separator[4],
                        Integer.valueOf(separator[5])));
            }
        }

        String[] separator = list.get(list.size() - 1).split(",");

        for (String s : separator) {
            int id = Integer.valueOf(s.trim());
            if (getSubtask(id) != null) super.getSubtask(id);
            else if (getEpic(id) != null) super.getEpic(id);
            else if (getTask(id) != null) super.getTask(id);
        }
    }

    public FileBackedTasksManager() {
        super();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save(task);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public Task getTask(int taskId) {
        Task task = getTasks().get(taskId);
        if (task != null) {
            getHistoryManager().add(getTasks().get(taskId));
            save(taskId);
            return getTasks().get(taskId);
        } else {
            return null;
        }
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
    }

    @Override
    public Epic getEpic(int epicId) {

        Epic epic = getEpics().get(epicId);
        if (epic != null) {
            getHistoryManager().add(getEpics().get(epicId));
            save(epicId);
            return getEpics().get(epicId);
        } else {
            return null;
        }
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save(subtask);
    }


    @Override
    public void updateSubtask(int subtaskId, Subtask subtask) {
        super.updateSubtask(subtaskId, subtask);
    }

    @Override
    public Subtask getSubtask(int subtaskId) {

        Subtask subtask = getSubtasks().get(subtaskId);
        if (subtask != null) {
            getHistoryManager().add(getSubtasks().get(subtaskId));
            save(subtaskId);
            return getSubtasks().get(subtaskId);
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = super.getEpics().get(epicId);
        List<Integer> subtasksFromEpic = epic.getSubtaskIds();
        for (Integer x : subtasksFromEpic) {
            save(super.getSubtasks().get(x).getId());
        }
        return super.getSubtasksByEpicId(epicId);
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
    }

    @Override
    public Collection<Task> getAllTask() {
        return super.getAllTask();
    }

    @Override
    public Collection<Subtask> getAllSubask() {
        return super.getAllSubask();
    }

    @Override
    public Collection<Epic> getAllEpic() {
        return super.getAllEpic();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}

