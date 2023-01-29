package ru.yandex.practicum.vyunnikov.taskManager.managers.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.vyunnikov.taskManager.managers.file.FileBackedTasksManager;
import ru.yandex.practicum.vyunnikov.taskManager.managers.http.adapters.DurationAdapter;
import ru.yandex.practicum.vyunnikov.taskManager.managers.http.adapters.LocalDateAdapter;
import ru.yandex.practicum.vyunnikov.taskManager.task.Epic;
import ru.yandex.practicum.vyunnikov.taskManager.task.Subtask;
import ru.yandex.practicum.vyunnikov.taskManager.task.Task;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;
    String url;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskManager(String url) {
        super(null);
        this.client = new KVTaskClient(url);
        this.url = url;
        loadFromServer();
    }

    @Override
    public void save() {
        String jsonTasks = gson.toJson(getAllTask());
        client.put("tasks", jsonTasks);
        String jsonEpics = gson.toJson(getAllEpic());
        client.put("epics", jsonEpics);
        String jsonSubTask = gson.toJson(getAllSubtask());
        client.put("subtasks", jsonSubTask);
        String jsonHistoryView = gson.toJson(getHistory());
        client.put("history", jsonHistoryView);
    }

    public void loadFromServer() {
        int maxId = 0;
        Type tasksType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> allTasks = gson.fromJson(client.load("tasks"), tasksType);
        if (allTasks != null) {
            for (Task task : allTasks) {
                tasks.put(task.getId(), task);
                addNewPrioritizedTask(task);
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
            }
        }

        Type epicsType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> allEpics = gson.fromJson(client.load("epics"), epicsType);
        if (allEpics != null) {
            for (Epic epic : allEpics) {
                epics.put(epic.getId(), epic);
                if (epic.getId() > maxId) {
                    maxId = epic.getId();
                }
            }
        }

        Type subtasksType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> allSubtasks = gson.fromJson(client.load("subtasks"), subtasksType);
        if (allSubtasks != null) {
            for (Subtask subtask : allSubtasks) {
                subtasks.put(subtask.getId(), subtask);
                addNewPrioritizedTask(subtask);
                int epicIdOfSubtask = subtask.getEpicId();
                Epic epic = epics.get(epicIdOfSubtask);
                Integer subtaskId = subtask.getId();
                System.out.println(subtaskId);
                if (subtaskId != null) {
                    epic.addSubTaskId(subtaskId);
                }
                if (subtask.getId() > maxId) {
                    maxId = subtask.getId();
                }
            }
        }

        setId(maxId);

        Type historyType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(client.load("history"), historyType);
        if (history != null) {
            for (Task objekt : history) {
                if (epics.containsKey(objekt.getId())) {
                    historyManager.add(epics.get(objekt.getId()));
                } else if (subtasks.containsKey(objekt.getId())) {
                    historyManager.add(subtasks.get(objekt.getId()));
                } else if (tasks.containsKey(objekt.getId())) {
                    historyManager.add(tasks.get(objekt.getId()));
                }
            }
        }
    }
}
