package taskManager.manager.file;

import taskManager.task.Epic;
import taskManager.task.Status;
import taskManager.task.Subtask;
import taskManager.task.Task;

import java.util.List;

public class CsvConverter {


    public static String taskToString(Task task) {
        return String.format("%s,%s,%s,%s,%s\n", task.getId(), TaskType.TASK, task.getTitle(),
                task.getStatus(), task.getDescription());
    }

    public static String taskToString(Epic epic) {
        return String.format("%s,%s,%s,%s,%s\n", epic.getId(), TaskType.EPIC, epic.getTitle(),
                epic.getStatus(), epic.getDescription());
    }

    public static String taskToString(Subtask subtask) {
        return String.format("%s,%s,%s,%s,%s,%s\n", subtask.getId(), TaskType.SUBTASK, subtask.getTitle(),
                subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
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

        return new Task(status, title, description, id);
    }

    public static Epic stringToEpic(String[] arrayOfStrings) {
        int id = Integer.parseInt(arrayOfStrings[0].trim());
        String title = arrayOfStrings[2];
        String description = arrayOfStrings[4];
        Status status = Status.valueOf(arrayOfStrings[3]);

        return new Epic(status, title, description, id);
    }

    public static Subtask stringToSubtask(String[] arrayOfStrings) {
        int id = Integer.parseInt(arrayOfStrings[0].trim());
        String title = arrayOfStrings[2];
        String description = arrayOfStrings[4];
        Status status = Status.valueOf(arrayOfStrings[3]);
        int epicId = Integer.parseInt(arrayOfStrings[5]);

        return new Subtask(status, title, description, id, epicId);
    }

}
