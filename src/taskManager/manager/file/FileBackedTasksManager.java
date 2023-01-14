package taskManager.manager.file;

import taskManager.exceptions.ManagerSaveException;
import taskManager.manager.task.InMemoryTaskManager;
import taskManager.task.Epic;
import taskManager.task.Subtask;
import taskManager.task.Task;

import java.io.*;
import java.util.*;

import static taskManager.manager.file.CsvConverter.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final String pathToFile;

    public FileBackedTasksManager(File file) {
        pathToFile = file.toString();
        loadFromFile(file);
    }

    private void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile))) {
            writer.write("id, тип, название, статус, описание, EpicId, startTime, duration");
            writer.newLine();

            for (Task task : getAllTask()) {
                writer.write(taskToString(task));
            }
            for (Epic epic : getAllEpic()) {
                writer.write(taskToString(epic));
            }
            for (Subtask subtask : getAllSubask()) {
                writer.write(taskToString(subtask));
            }

            writer.newLine();
            writer.write(historyToString(getHistory()));

        } catch (IOException exception) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла, но " +
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
            throw new ManagerSaveException("Произошла ошибка во время считывания информации из файла, но " +
                    "вы смогли увидеть моё собственное расчудесное непроверяемое исключение");
        }
        if (!list.isEmpty()) {
            int maxId = 0;
            for (int i = 1; i < list.size() - 2; i++) {

                String[] arrayOfStrings = list.get(i).split(",");
                int id = Integer.parseInt(arrayOfStrings[0].trim());

                if (id > maxId) {
                    maxId = id;
                }
                if (Objects.equals(arrayOfStrings[1], String.valueOf(TaskType.TASK))) {
                    tasks.put(id, stringToTask(arrayOfStrings));
                } else if (Objects.equals(arrayOfStrings[1], String.valueOf(TaskType.EPIC))) {
                    epics.put(id, stringToEpic(arrayOfStrings));
                } else if (Objects.equals(arrayOfStrings[1], String.valueOf(TaskType.SUBTASK))) {
                    subtasks.put(id, stringToSubtask(arrayOfStrings));
                }
            }

            for (Subtask value : subtasks.values()) {
                Epic epic = epics.get(value.getEpicId());
                if (epic != null) {
                    epic.addSubTaskId(value.getId());
                }
            }

            setId(maxId);

            String historyString = list.get(list.size() - 1);

            for (Integer id : stringToListInteger(historyString)) {
                if (subtasks.containsKey(id)) {
                    historyManager.add(subtasks.get(id));
                } else if (epics.containsKey(id)) {
                    historyManager.add(epics.get(id));
                } else if (tasks.containsKey(id)) {
                    historyManager.add(tasks.get(id));
                }
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