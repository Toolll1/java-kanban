package ru.yandex.practicum.vyunnikov.taskManager.managers.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {
    private final String url;
    private final String apiToken;

    public KVTaskClient(String url) {
        this.url = url;
        apiToken = registration(url);
    }

    public void put (String key, String json) {

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                    .header("Content-Type", "application/json")
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String load(String key) {
        try {
            HttpClient client  = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return response.body();
        }  catch (IOException | InterruptedException e) {
            System.out.println("вероятно, КВ сервер не запущен");
            e.printStackTrace();
        }
        return null;
    }

    public String registration(String url) {
        try {
            HttpClient client  = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(URI.create(url + "register"))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
            return send.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
