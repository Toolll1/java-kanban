package ru.yandex.practicum.Viyunnikov.taskManager.manager.file;

import ru.yandex.practicum.Viyunnikov.taskManager.task.Epic;
import ru.yandex.practicum.Viyunnikov.taskManager.task.Status;
import ru.yandex.practicum.Viyunnikov.taskManager.task.Subtask;
import ru.yandex.practicum.Viyunnikov.taskManager.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvConverter {

    public static String taskToString(Task task) {
        return String.format("%s,%s,%s,%s,%s,%s,%s\n", task.getId(), TaskType.TASK, task.getTitle(),
                task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration());
    }

    public static String taskToString(Epic epic) {
        return String.format("%s,%s,%s,%s,%s,%s,%s\n", epic.getId(), TaskType.EPIC, epic.getTitle(),
                epic.getStatus(), epic.getDescription(), epic.getStartTime(), epic.getDuration());
    }

    public static String taskToString(Subtask subtask) {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", subtask.getId(), TaskType.SUBTASK, subtask.getTitle(),
                subtask.getStatus(), subtask.getDescription(), subtask.getEpicId(), subtask.getStartTime(),
                subtask.getDuration());
    }

    public static String historyToString(List<Task> history) {
        StringBuilder sb = new StringBuilder();
        for (Task task : history) {
            sb.append(task.getId()).append(",");
        }
        return sb.toString();
    }


    public static Task stringToTask(String[] arrayOfStrings) {
        int id = Integer.parseInt(arrayOfStrings[0].trim());
        String title = arrayOfStrings[2];
        String description = arrayOfStrings[4];
        Status status = Status.valueOf(arrayOfStrings[3]);
        LocalDateTime startTime = LocalDateTime.parse(arrayOfStrings[5]);
        Duration duration = Duration.parse((arrayOfStrings[6]));

        return new Task(status, title, description, id, startTime, duration);
    }

    public static Epic stringToEpic(String[] arrayOfStrings) {
        int id = Integer.parseInt(arrayOfStrings[0].trim());
        String title = arrayOfStrings[2];
        String description = arrayOfStrings[4];
        Status status = Status.valueOf(arrayOfStrings[3]);
        LocalDateTime startTime = LocalDateTime.parse(arrayOfStrings[5]);
        Duration duration = Duration.parse((arrayOfStrings[6]));

        return new Epic(status, title, description, id, startTime, duration);
    }

    public static Subtask stringToSubtask(String[] arrayOfStrings) {
        int id = Integer.parseInt(arrayOfStrings[0].trim());
        String title = arrayOfStrings[2];
        String description = arrayOfStrings[4];
        Status status = Status.valueOf(arrayOfStrings[3]);
        int epicId = Integer.parseInt(arrayOfStrings[5]);
        LocalDateTime startTime = LocalDateTime.parse(arrayOfStrings[6]);
        Duration duration = Duration.parse((arrayOfStrings[7]));

        return new Subtask(status, title, description, id, epicId, startTime, duration);
    }

    public static List<Integer> stringToListInteger(String historyString) {
        List<Integer> result = new ArrayList<>();
        String[] taskId = historyString.split(",");

        for (String s : taskId) {
            result.add(Integer.valueOf(s.trim()));
        }
        return result;
    }

}
