package ru.yandex.practicum.vyunnikov.taskManager.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.vyunnikov.taskManager.manager.Managers;
import ru.yandex.practicum.vyunnikov.taskManager.manager.file.FileBackedTasksManager;
import ru.yandex.practicum.vyunnikov.taskManager.task.Epic;
import ru.yandex.practicum.vyunnikov.taskManager.task.Subtask;
import ru.yandex.practicum.vyunnikov.taskManager.task.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    FileBackedTasksManager taskManager;

    public HttpTaskServer(File file) throws IOException {
        taskManager = Managers.getDefaultFileBackedTasksManager(file);
        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new PostsHandler());
        httpServer.start(); // запускаем сервер

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    class PostsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String path = exchange.getRequestURI().getPath();
            String taskType = path.split("/")[2];
            String query = exchange.getRequestURI().getQuery();
            String method = exchange.getRequestMethod();
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

            switch (taskType) {
                case "task" -> handleTask(exchange, method, body, query);
                case "subtask" -> handleSubtask(exchange, method, body, query);
                case "epic" -> handleEpic(exchange, method, body, query);
                default -> writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private void handleTask(HttpExchange exchange, String method, String body, String query) throws IOException {

        List<Task> allTasks = (List<Task>) taskManager.getAllTask();
        String badRequest = "Сервер не понимает запрос или пытается его обработать, " +
                "но не может выполнить из-за того, что какой-то его аспект неверен.";

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        switch (method) {

            case "GET" -> {
                if (query != null) {
                    int id;

                    try {
                        id = Integer.parseInt(query.split("=")[1]);
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "передано не число", 400);
                        return;
                    }

                    if (allTasks.contains(taskManager.getTask(id))) {
                        Task task = taskManager.getTask(id);
                        String postSerialized = "";

                        try {
                            postSerialized = gson.toJson(task);
                        } catch (Exception e) {
                            writeResponse(exchange, badRequest, 400);
                        }

                        writeResponse(exchange, postSerialized, 200);
                        return;
                    } else {
                        writeResponse(exchange, "задачи с таким id не существует", 400);
                        return;
                    }
                } else {
                    String response = gson.toJson(allTasks);
                    writeResponse(exchange, response, 200);
                    return;
                }
            }

            case "POST" -> {
                Task task = null;

                try {
                    task = gson.fromJson(body, Task.class);
                } catch (Exception exception) {
                    writeResponse(exchange, badRequest, 400);
                }

                if (query != null) {
                    int id;

                    try {
                        id = Integer.parseInt(query.split("=")[1]);
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "передано не число", 400);
                        return;
                    }

                    if (task.getId() != null && task.getId() != id) {
                        writeResponse(exchange, "в запросе и в теле переданы разные id", 400);
                        return;
                    }

                    if (allTasks.contains(taskManager.getTask(id))) {
                        task.setId(id);
                        taskManager.updateTask(task);
                        writeResponse(exchange, "задача успешно изменена.", 200);
                    } else {
                        writeResponse(exchange, "нет такой задачи", 400);
                        return;
                    }
                } else {
                    if (task.getId() != null) {
                        taskManager.createTask(task);
                        writeResponse(exchange, "задача успешно добавлена. Id задачи изменен на "
                                + task.getId(), 200);
                    } else {
                        taskManager.createTask(task);
                        writeResponse(exchange, "задача успешно добавлена.", 200);
                    }
                }
                return;
            }

            case "DELETE" -> {

                if (query != null) {
                    int id;

                    try {
                        id = Integer.parseInt(query.split("=")[1]);
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "передано не число", 400);
                        return;
                    }
                    if (allTasks.contains(taskManager.getTask(id))){
                        taskManager.deleteTask(id);
                        writeResponse(exchange, "задача успешно удалена.", 200);
                    } else {
                        writeResponse(exchange, "нет такой задачи", 400);
                    }

                } else {
                    taskManager.deleteAllTask();
                    writeResponse(exchange, "все задачи успешно удалены.", 200);
                }
            }
        }


        writeResponse(exchange, "Пост с идентификатором " + " не найден", 400);
    }

    private void handleSubtask(HttpExchange exchange, String method, String body, String query) throws IOException {
        List<Subtask> allSubtasks = (List<Subtask>) taskManager.getAllSubtask();

        switch (method) {
            case "GET" -> {
                if (query != null) {
                    int id;

                    try {
                        id = Integer.parseInt(query.split("=")[1]);
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "передано не число", 400);
                        return;
                    }

                    if (allSubtasks.contains(taskManager.getSubtask(id))) {
                        Subtask subtask = taskManager.getSubtask(id);
                        String response = subtask.toString();
                        writeResponse(exchange, response, 200);
                        return;
                    } else {
                        writeResponse(exchange, "задачи с таким id не существует", 400);
                        return;
                    }

                } else {
                    String response = allSubtasks.toString();
                    writeResponse(exchange, response, 200);
                    return;
                }
            }
        }
    }

    private void handleEpic(HttpExchange exchange, String method, String body, String query) throws IOException {
        List<Epic> allEpics = (List<Epic>) taskManager.getAllEpic();

        switch (method) {
            case "GET" -> {
                if (query != null) {
                    int id;

                    try {
                        id = Integer.parseInt(query.split("=")[1]);
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "передано не число", 400);
                        return;
                    }

                    if (allEpics.contains(taskManager.getEpic(id))) {
                        Epic epic = taskManager.getEpic(id);
                        String response = epic.toString();
                        writeResponse(exchange, response, 200);
                        return;
                    } else {
                        writeResponse(exchange, "задачи с таким id не существует", 400);
                        return;
                    }

                } else {
                    String response = allEpics.toString();
                    writeResponse(exchange, response, 200);
                    return;
                }
            }
          /*  case "POST" -> {
                try {
                    Task task = gson.fromJson(body, Task.class);
                    taskManager.createTask(task);
                    exchange.sendResponseHeaders(200, 0);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }
            case "DELETE" -> {
                if (query != null) {
                    String idTask = query.substring(3);
                    taskManager.deleteTask(Integer.parseInt(idTask));
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    taskManager.deleteTasks();
                    exchange.sendResponseHeaders(200, 0);
                }
            }
        }

        writeResponse(exchange, "Пост с идентификатором " + postId + " не найден", 404);*/
        }
    }


}