package taskManager.manager.file;

import taskManager.task.Epic;
import taskManager.task.Status;
import taskManager.task.Subtask;
import taskManager.task.Task;

public class CsvConverter {




    public String taskToString(Task task) {
        return String.format("%s,%s,%s,%s,%s\n", task.getId(), TaskType.TASK, task.getTitle(),
                task.getStatus(), task.getDescription());
    }

    public String taskToString(Epic epic) {
        return String.format("%s,%s,%s,%s,%s\n", epic.getId(), TaskType.EPIC, epic.getTitle(),
                epic.getStatus(), epic.getDescription());
    }

    public String taskToString(Subtask subtask) {
        return String.format("%s,%s,%s,%s,%s,%s\n", subtask.getId(), TaskType.SUBTASK, subtask.getTitle(),
                subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
    }

    public String historyToString(Task task) {
        return (task.getId() + ",");
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
