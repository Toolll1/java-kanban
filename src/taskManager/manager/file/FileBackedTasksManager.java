package taskManager.manager.file;

import taskManager.exceptions.ManagerSaveException;
import taskManager.manager.task.InMemoryTaskManager;
import taskManager.task.Epic;
import taskManager.task.Subtask;
import taskManager.task.Task;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

/*
* с учетом того, что нужно оставить только один конструктор, это решение - лучшее из того, что пришло вне в голову))
* loadFromFile(file) запускается только в том случае, если мы хотим считать файл
* */

public class FileBackedTasksManager extends InMemoryTaskManager {

    CsvConverter csvConverter = new CsvConverter();

    private FileBackedTasksManager(File file, boolean CheckReader) {
        if (CheckReader) {
            loadFromFile(file);
        }
    }

    public static FileBackedTasksManager getTaskManagerForFile(File file, boolean CheckReader) {
        return new FileBackedTasksManager(file, CheckReader);
    }

    private void save() {
        String path = String.valueOf(Path.of("src", "taskManager", "task", "file.csv"));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("id, тип, название, статус, описание, EpicId");
            writer.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                Task task = tasks.get(entry.getKey());
                writer.write(csvConverter.taskToString(task));
            }

            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                Epic epic = epics.get(entry.getKey());
                writer.write(csvConverter.taskToString(epic));
            }
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                Subtask subtask = subtasks.get(entry.getKey());
                writer.write(csvConverter.taskToString(subtask));
            }

            List<Task> history = getHistory();
            writer.newLine();

            for (Task task : history) {
                writer.write(csvConverter.historyToString(task));
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла и " +
                    "вы смогли увидеть моё собственное непроверяемое исключение");
        }
    }

    private void loadFromFile(File file) {

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

        for (int i = 1; i < list.size() - 2; i++) {

            String[] arrayOfStrings = list.get(i).split(",");
            int id = Integer.parseInt(arrayOfStrings[0].trim());

            if (Objects.equals(arrayOfStrings[1], "TASK")) {
                tasks.put(id, (CsvConverter.stringToTask(arrayOfStrings)));
                newId();
            } else if (Objects.equals(arrayOfStrings[1], "EPIC")) {
                epics.put(id, CsvConverter.stringToEpic(arrayOfStrings));
                newId();
            } else if (Objects.equals(arrayOfStrings[1], "SUBTASK")) {
                subtasks.put(id, CsvConverter.stringToSubtask(arrayOfStrings));
                newId();
            }
        }

        String[] taskId = list.get(list.size() - 1).split(",");
        for (String s : taskId) {
            int id = Integer.parseInt(s.trim());

            if (subtasks.containsKey(id)) {
                getSubtask(id);
            } else if (epics.containsKey(id)) {
                super.getEpic(id);
            } else if (tasks.containsKey(id)) {
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