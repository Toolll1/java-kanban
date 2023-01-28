package ru.yandex.practicum.vyunnikov.taskManager.managers.file;

import ru.yandex.practicum.vyunnikov.taskManager.exceptions.ManagerSaveException;
import ru.yandex.practicum.vyunnikov.taskManager.managers.task.InMemoryTaskManager;
import ru.yandex.practicum.vyunnikov.taskManager.task.Epic;
import ru.yandex.practicum.vyunnikov.taskManager.task.Subtask;
import ru.yandex.practicum.vyunnikov.taskManager.task.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.yandex.practicum.vyunnikov.taskManager.managers.file.CsvConverter.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private String pathToFile = "";

    public FileBackedTasksManager(File file) {
      if (file != null) {
          pathToFile = file.toString();
          loadFromFile(file);
      }
    }

    public void save() {
        if (tasks.size() == 0 && epics.size() == 0 && subtasks.size() == 0) {
            try {
                new FileWriter(pathToFile, false).close();
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        }

        if (tasks.size() > 0 || epics.size() > 0 || subtasks.size() > 0) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile))) {
                writer.write("id, тип, название, статус, описание, EpicId, startTime, duration");
                writer.newLine();

                for (Task task : getAllTask()) {
                    writer.write(taskToString(task));
                }
                for (Epic epic : getAllEpic()) {
                    writer.write(taskToString(epic));
                }
                for (Subtask subtask : getAllSubtask()) {
                    writer.write(taskToString(subtask));
                }

                writer.newLine();
                if (getHistory().size() > 0) writer.write(historyToString(getHistory()));

            } catch (IOException exception) {
                throw new ManagerSaveException("Произошла ошибка во время записи файла, но " +
                        "вы смогли увидеть моё собственное непроверяемое исключение");
            }
        }
    }

    public void loadFromFile(File file) {
        if (file.length() > 0) {
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

            int lastLine = list.size() - 2;

            if (list.get(list.size() - 1).isEmpty()) lastLine = list.size() - 1;

            int maxId = 0;
            for (int i = 1; i < lastLine; i++) {

                String[] arrayOfStrings = list.get(i).split(",");
                int id = Integer.parseInt(arrayOfStrings[0].trim());

                if (id > maxId) {
                    maxId = id;
                }

                if (Objects.equals(arrayOfStrings[1], String.valueOf(TaskType.TASK))) {
                    tasks.put(id, stringToTask(arrayOfStrings));
                    addNewPrioritizedTask(stringToTask(arrayOfStrings));
                } else if (Objects.equals(arrayOfStrings[1], String.valueOf(TaskType.EPIC))) {
                    epics.put(id, stringToEpic(arrayOfStrings));
                } else if (Objects.equals(arrayOfStrings[1], String.valueOf(TaskType.SUBTASK))) {
                    Subtask subtask = stringToSubtask(arrayOfStrings);
                    int epicIdOfSubtask = subtask.getEpicId();
                    Epic epic = epics.get(epicIdOfSubtask);
                    int subtaskId = subtask.getId();

                    epic.addSubTaskId(subtaskId);
                    subtasks.put(id, subtask);
                    addNewPrioritizedTask(subtask);
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
            if (historyString.isEmpty()) return;

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
        if (super.validateTaskPriority(task)) {
            super.createTask(task);
            save();
        }
    }

    @Override
    public void updateTask(Task task){
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
        if (super.validateTaskPriority(subtask)) {
            super.createSubtask(subtask);
            save();
        }
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
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