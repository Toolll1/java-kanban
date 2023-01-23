package ru.yandex.practicum.vyunnikov.taskManager.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.vyunnikov.taskManager.manager.Managers;
import ru.yandex.practicum.vyunnikov.taskManager.manager.file.FileBackedTasksManager;
import ru.yandex.practicum.vyunnikov.taskManager.task.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new Gson();
     FileBackedTasksManager taskManager;

    public HttpTaskServer(File file) throws IOException {
                taskManager = Managers.getDefaultFileBackedTasksManager(file);

        System.out.println(taskManager.getAllTask());

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
            System.out.println(taskManager.getAllTask());

            switch (taskType) {
                case "task" -> handleTask(exchange, method, body, query);
                case "subtask" -> handleSubtask(exchange);
                case "epic" -> handleEpic(exchange);
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
        switch (method) {
            case "GET" -> {
                if (query != null) {
                    int id = Integer.parseInt(query.split("=")[1]);
                    Task task = taskManager.getTask(id);
                    String response = task.toString();

                    writeResponse(exchange, response, 200);
                    return;
                } else {
                    System.out.println(taskManager.getAllTask());
                    String response = taskManager.getAllTask().toString();
                    writeResponse(exchange, response, 200);
                    System.out.println(taskManager.getAllTask());
                    return;
                }
            }
          /* case "POST" -> {
                try {
                    Task task = gson.fromJson(body, Task.class);
                    taskManager.addTasks(task);
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
            }*/
        }

     /*   HttpResponse<String> response = client.send(path, HttpResponse.BodyHandlers.ofString());
        // проверяем, успешно ли обработан запрос
        if (response.statusCode() == 200) {
            // передаем парсеру тело ответа в виде строки, содержащей данные в формате JSON
            JsonElement jsonElement = JsonParser.parseString(response.body());
            if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                System.out.println("Ответ от сервера не соответствует ожидаемому.");
                return;
            }
            // преобразуем результат разбора текста в JSON-объект
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            System.out.println(jsonElement);
            System.out.println(jsonObject);
            // получаем название страны
            String country = jsonObject.get("country").getAsString();
        }

        Optional<Integer> postIdOpt = getPostId(exchange);
        if (postIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор поста", 400);
            return;
        }
        int postId = postIdOpt.get();

        for (Post post : posts) {
            if (post.getId() == postId) {
                String commentsJson = gson.toJson(post.getCommentaries());
                writeResponse(exchange, commentsJson, 200);
                return;
            }
        } */

        writeResponse(exchange, "Пост с идентификатором " + " не найден", 400);
    }

    private static void handleSubtask(HttpExchange exchange) throws IOException {
        /*String requestMethod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        switch (requestMethod) {
            case "GET" -> {
                if (query != null) {
                    String idTask = query.substring(3);
                    Task task = taskManager.getTask(Integer.parseInt(idTask));
                    String response = gson.toJson(task);
                    writeResponse(exchange, response,200);
                    return;
                } else {
                    String response = gson.toJson(taskManager.getAllTask());
                    writeResponse(exchange, response,200);
                    return;
                }
            }
            case "POST" -> {
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

    private static void handleEpic(HttpExchange exchange) throws IOException {
   /*     Optional<Integer> postIdOpt = getPostId(exchange);
        if(postIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор поста", 400);
            return;
        }
        int postId = postIdOpt.get();

        for (Post post : posts) {
            if (post.getId() == postId) {
                String commentsJson = gson.toJson(post.getCommentaries());
                writeResponse(exchange, commentsJson, 200);
                return;
            }
        }

        writeResponse(exchange, "Пост с идентификатором " + postId + " не найден", 404);
    }*/

    }

}