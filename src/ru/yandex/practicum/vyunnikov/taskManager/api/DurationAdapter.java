package ru.yandex.practicum.vyunnikov.taskManager.api;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {


    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {

        if (duration == null) {
            jsonWriter.value(String.valueOf(Duration.ofMinutes(0)));
        } else {
            jsonWriter.value(String.valueOf(duration.toMinutes()));
        }

    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        String x = jsonReader.nextString();
        Duration y = Duration.ofMinutes(Long.parseLong(x));

        return y;
    }
}
