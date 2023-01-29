package ru.yandex.practicum.vyunnikov.taskManager.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.vyunnikov.taskManager.exceptions.ValidateException;
import ru.yandex.practicum.vyunnikov.taskManager.managers.Managers;
import ru.yandex.practicum.vyunnikov.taskManager.managers.http.adapters.DurationAdapter;
import ru.yandex.practicum.vyunnikov.taskManager.managers.http.adapters.LocalDateAdapter;
import ru.yandex.practicum.vyunnikov.taskManager.managers.task.TaskManager;
import ru.yandex.practicum.vyunnikov.taskManager.task.Epic;
import ru.yandex.practicum.vyunnikov.taskManager.task.Subtask;
import ru.yandex.practicum.vyunnikov.taskManager.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String badRequest = "Сервер не понимает запрос или пытается его обработать, "
            + "но не может выполнить из-за того, что какой-то его аспект неверен.";
    private static TaskManager taskManager;
    HttpServer httpServer;
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskServer(String url) throws IOException {
        taskManager = Managers.getDefault(url);
        httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new PostsHandler());
        start();
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
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
                case "history" -> handleHistory(exchange, method);
                default -> writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }
    }

    private void handleHistory(HttpExchange exchange, String method) throws IOException {

        if (method.equals("GET")) {
            String response = gson.toJson(taskManager.getHistory());
            writeResponse(exchange, response, 200);
        } else {
            writeResponse(exchange, "Обработка метода " + method + " не настроена", 400);
        }
    }


    private void handleTask(HttpExchange exchange, String method, String body, String query) throws IOException {
        switch (method) {
            case "GET" -> processingTheGetMethodForTasks(exchange, query);
            case "POST" -> processingThePostMethodForTasks(exchange, query, body);
            case "DELETE" -> processingTheDeleteMethodForTasks(exchange, query);
            default -> writeResponse(exchange, "Обработка метода " + method + " не настроена", 400);
        }
    }

    private void handleSubtask(HttpExchange exchange, String method, String body, String query) throws IOException {
        switch (method) {
            case "GET" -> processingTheGetMethodForSubtasks(exchange, query);
            case "POST" -> processingThePostMethodForSubtask(exchange, query, body);
            case "DELETE" -> processingTheDeleteMethodForSubtask(exchange, query);
            default -> writeResponse(exchange, "Обработка метода " + method + " не настроена", 400);
        }
    }

    private void handleEpic(HttpExchange exchange, String method, String body, String query) throws IOException {
        switch (method) {
            case "GET" -> processingTheGetMethodForEpic(exchange, query);
            case "POST" -> processingThePostMethodForEpic(exchange, query, body);
            case "DELETE" -> processingTheDeleteMethodForEpic(exchange, query);
            default -> writeResponse(exchange, "Обработка метода " + method + " не настроена", 400);
        }
    }


    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
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

    private void processingTheGetMethodForTasks(HttpExchange exchange, String query) throws IOException {
        if (query != null) {
            try {
                int id = Integer.parseInt(query.split("=")[1]);

                if (taskManager.getTask(id) != null) {
                    Task task = taskManager.getTask(id);
                    String postSerialized = gson.toJson(task);
                    writeResponse(exchange, postSerialized, 200);
                } else {
                    writeResponse(exchange, "задачи с таким id не существует", 400);
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
                writeResponse(exchange, "передано не число", 400);
            } catch (Exception e) {
                e.printStackTrace();
                writeResponse(exchange, badRequest, 400);
            }
        } else {
            String response = gson.toJson(taskManager.getAllTask());
            writeResponse(exchange, response, 200);
        }
    }

    private void processingThePostMethodForTasks(HttpExchange exchange, String query, String body) throws IOException {
        Task task;
        int id;

        try {
            id = Integer.parseInt(query.split("=")[1]);
            task = gson.fromJson(body, Task.class);

            if (query != null) {
                if (taskManager.getTask(id) != null) {
                    task.setId(id);
                    taskManager.updateTask(task);
                    writeResponse(exchange, "задача успешно изменена.", 200);
                } else {
                    writeResponse(exchange, "нет такой задачи", 404);
                }
            } else {
                taskManager.createTask(task);
            }
        } catch (ValidateException e) {
            writeResponse(exchange, "Произошла ошибка добавления/изменения задачи, "
                    + "ввиду пересечения по времени с другой задачей", 400);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            writeResponse(exchange, "передано не число", 400);
        } catch (Exception e) {
            e.printStackTrace();
            writeResponse(exchange, badRequest, 400);
        }
    }

    private void processingTheDeleteMethodForTasks(HttpExchange exchange, String query) throws IOException {
        if (query != null) {
            int id;

            try {
                id = Integer.parseInt(query.split("=")[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                writeResponse(exchange, "передано не число", 400);
                return;
            }

            if (taskManager.getTask(id) != null) {
                taskManager.deleteTask(id);
                writeResponse(exchange, "задача успешно удалена.", 200);
            } else {
                writeResponse(exchange, "нет такой задачи", 404);
            }

        } else {
            taskManager.deleteAllTask();
            writeResponse(exchange, "все задачи успешно удалены.", 200);
        }
    }

    private void processingTheDeleteMethodForSubtask(HttpExchange exchange, String query) throws IOException {
        if (query != null) {
            int id;

            try {
                id = Integer.parseInt(query.split("=")[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                writeResponse(exchange, "передано не число", 400);
                return;
            }
            if (taskManager.getSubtask(id) != null) {
                taskManager.deleteSubtask(id);
                writeResponse(exchange, "подзадача успешно удалена.", 200);
            } else {
                writeResponse(exchange, "нет такой подзадачи", 404);
            }

        } else {
            taskManager.deleteAllSubtasks();
            writeResponse(exchange, "все подзадачи успешно удалены.", 200);
        }
    }

    private void processingThePostMethodForSubtask(HttpExchange exchange, String query, String body) throws IOException {
        Subtask subtask;
        int id;

        try {
            subtask = gson.fromJson(body, Subtask.class);
            id = Integer.parseInt(query.split("=")[1]);

            if (query != null) {
                if (taskManager.getSubtask(id) != null) {
                    subtask.setId(id);
                    taskManager.updateSubtask(subtask);
                    writeResponse(exchange, "подзадача успешно изменена.", 200);
                } else {
                    writeResponse(exchange, "нет такой подзадачи", 404);
                }
            } else {
                taskManager.createSubtask(subtask);
            }
        } catch (ValidateException e) {
            writeResponse(exchange, "Произошла ошибка добавления/изменения позадачи, "
                    + "ввиду пересечения по времени с другой задачей", 400);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            writeResponse(exchange, "передано не число", 400);
        } catch (Exception e) {
            e.printStackTrace();
            writeResponse(exchange, badRequest, 400);
        }
    }

    private void processingTheGetMethodForSubtasks(HttpExchange exchange, String query) throws IOException {
        if (query != null) {
            try {
                int id = Integer.parseInt(query.split("=")[1]);

                if (taskManager.getSubtask(id) != null) {
                    Subtask subtask = taskManager.getSubtask(id);
                    String postSerialized = gson.toJson(subtask);

                    writeResponse(exchange, postSerialized, 200);
                } else {
                    writeResponse(exchange, "подзадачи с таким id не существует", 400);
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
                writeResponse(exchange, "передано не число", 400);
            } catch (Exception e) {
                e.printStackTrace();
                writeResponse(exchange, badRequest, 400);
            }
        } else {
            String response = gson.toJson(taskManager.getAllSubtask());
            writeResponse(exchange, response, 200);
        }
    }

    private void processingTheDeleteMethodForEpic(HttpExchange exchange, String query) throws IOException {
        if (query != null) {
            int id;

            try {
                id = Integer.parseInt(query.split("=")[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                writeResponse(exchange, "передано не число", 400);
                return;
            }
            if (taskManager.getEpic(id) != null) {
                taskManager.deleteEpic(id);
                writeResponse(exchange, "эпик успешно удален.", 200);
            } else {
                writeResponse(exchange, "нет такого эпика", 404);
            }

        } else {
            taskManager.deleteAllEpics();
            writeResponse(exchange, "все эпики успешно удалены.", 200);
        }
    }

    private void processingThePostMethodForEpic(HttpExchange exchange, String query, String body) throws IOException {
        Epic epic = null;
        int id = 0;

        try {
            epic = gson.fromJson(body, Epic.class);
            id = Integer.parseInt(query.split("=")[1]);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            writeResponse(exchange, "передано не число", 400);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            writeResponse(exchange, badRequest, 400);
        }

        if (query != null) {
            if (taskManager.getEpic(id) != null) {
                epic.setId(id);
                epic.setSubtaskIds(taskManager.getEpic(id).getSubtaskIds());
                taskManager.updateEpic(epic);
                writeResponse(exchange, "эпик успешно изменен.", 200);
            } else {
                writeResponse(exchange, "нет такого эпика", 404);
            }
        } else {
            if (epic.getId() != null) {
                taskManager.createEpic(epic);
                taskManager.getAllEpic().add(epic);
                writeResponse(exchange, "эпик успешно добавлен. Id эпика изменен на " + epic.getId(), 200);
            } else {
                taskManager.createEpic(epic);
                writeResponse(exchange, "эпик успешно добавлен.", 200);
            }
        }
    }

    private void processingTheGetMethodForEpic(HttpExchange exchange, String query) throws IOException {
        if (query != null) {
            try {
                int id = Integer.parseInt(query.split("=")[1]);

                if (taskManager.getEpic(id) != null) {
                    Epic epic = taskManager.getEpic(id);
                    String postSerialized = gson.toJson(epic);

                    writeResponse(exchange, postSerialized, 200);
                } else {
                    writeResponse(exchange, "эпика с таким id не существует", 400);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                writeResponse(exchange, "передано не число", 400);
            } catch (Exception e) {
                e.printStackTrace();
                writeResponse(exchange, badRequest, 400);
            }
        } else {
            String response = gson.toJson(taskManager.getAllEpic());
            writeResponse(exchange, response, 200);
        }
    }
}