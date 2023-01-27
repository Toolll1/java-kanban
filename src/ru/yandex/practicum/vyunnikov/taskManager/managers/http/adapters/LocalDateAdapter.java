package ru.yandex.practicum.vyunnikov.taskManager.managers.http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        LocalDateTime value;
        if (localDateTime == null) {
            value = LocalDateTime.now();
        } else {
            value = localDateTime;
        }
        jsonWriter.value(value.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String x = jsonReader.nextString();
        LocalDateTime y = LocalDateTime.parse(x, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        return y;
    }
}
