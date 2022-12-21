package taskManager.manager.file;

import taskManager.exceptions.ManagerSaveException;
import taskManager.manager.task.InMemoryTaskManager;
import taskManager.task.Epic;
import taskManager.task.Status;
import taskManager.task.Subtask;
import taskManager.task.Task;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

/*
* "3) я бы в классе managers методу для получения FileBackedTasksManager добавил параметр имя файла,
* чтобы можно было задать с каким фалом работать"
*
* - добавить-добавил, но не понимаю, как это использовать..
*
*
* "4) конструктор FileBackedTasksManager лучше сделать приватным, а обьект класса создавать статическим методом,
* который будет возвращать FileBackedTasksManager а внутри считывать данные из файла, и заполнять себя"
*
*  - а тут совсем не понял.. И просто убрал конструктор)))
*
* */


public class FileBackedTasksManager extends InMemoryTaskManager {

    private void save() {
        String path = String.valueOf(Path.of("src", "taskManager", "task", "file.csv"));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("id, тип, название, статус, описание, EpicId");
            writer.newLine();
            writer.write(CsvConverter());
            writer.newLine();

            List<Task> history = getHistory();
            for (Task task : history) {
                writer.write(task.getId() + ",");
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла и " +
                    "вы смогли увидеть моё собственное непроверяемое исключение");
        }
    }

    private String CsvConverter() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Integer, Task> task : tasks.entrySet()) {
            Task x = tasks.get(task.getKey());
            sb.append(String.format("%s,%s,%s,%s,%s\n", x.getId(), TaskType.TASK, x.getTitle(),
                    x.getStatus(), x.getDescription()));
        }
        for (Map.Entry<Integer, Epic> epic : epics.entrySet()) {
            Epic x = epics.get(epic.getKey());
            sb.append(String.format("%s,%s,%s,%s,%s\n", x.getId(), TaskType.EPIC, x.getTitle(),
                    x.getStatus(), x.getDescription()));
        }
        for (Map.Entry<Integer, Subtask> subtask : subtasks.entrySet()) {
            Subtask x = subtasks.get(subtask.getKey());
            sb.append(String.format("%s,%s,%s,%s,%s,%s\n", x.getId(), TaskType.SUBTASK, x.getTitle(),
                    x.getStatus(), x.getDescription(), x.getEpicId()));
        }
        return sb.toString();
    }

    public void loadFromFile(File file) {

        List<String> list = new ArrayList<>();

        try (Reader fileReader = new FileReader(file); BufferedReader br = new BufferedReader(fileReader)) {
            while (br.ready()) {
                String line = br.readLine();
                list.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время считывания информации из файла и " +
                    "вы смогли увидеть моё собственное расчудесное непроверяемое исключение");
        }


        for (int i = 1; !list.get(i).isEmpty(); i++) {

            String[] arrayOfStrings = list.get(i).split(",");
            int id = Integer.parseInt(arrayOfStrings[0].trim());
            String title = arrayOfStrings[2];
            String description = arrayOfStrings[4];
            Status status = Status.valueOf(arrayOfStrings[3]);

            if (Objects.equals(arrayOfStrings[1], "TASK")) {
                tasks.put(id, (new Task(status, title, description, id)));
                newId();
            } else if (Objects.equals(arrayOfStrings[1], "EPIC")) {
                epics.put(id, (new Epic(status, title, description, id)));
                newId();
            } else if (Objects.equals(arrayOfStrings[1], "SUBTASK")) {
                int epicId = Integer.parseInt(arrayOfStrings[5]);
                subtasks.put(id, (new Subtask(status, title, description, id, epicId)));
                newId();
            }
        }

        String[] taskId = list.get(list.size() - 1).split(",");
        for (String s : taskId) {
            int id = Integer.parseInt(s.trim());

            if (subtasks.get(id) != null) {
                getSubtask(id);
            } else if (epics.get(id) != null) {
                super.getEpic(id);
            } else if (tasks.get(id) != null) {
                getTask(id);
            }
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Task getTask(int taskId) {
        Task task = super.getTask(taskId);
        save();
        return task;
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = super.getEpic(epicId);
        save();
        return epic;
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }


    @Override
    public void updateSubtask(int subtaskId, Subtask subtask) {
        super.updateSubtask(subtaskId, subtask);
        save();
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subtask = super.getSubtask(subtaskId);
        save();
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        List<Integer> subtasksFromEpic = epic.getSubtaskIds();
        for (Integer x : subtasksFromEpic) {
            listOfSubtasks.add(subtasks.get(x));
        }
        save();
        return listOfSubtasks;
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

}