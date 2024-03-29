package ru.yandex.practicum.vyunnikov.taskManager.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    protected Integer epicId;

    public Subtask(Status status, String title, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(status, title, description, epicId, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Status status, String title, String description, int Id, Integer epicId, LocalDateTime startTime, Duration duration) {
        super(status, title, description, Id, startTime, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(title, subtask.title) && Objects.equals(description, subtask.description)
                && status == subtask.status && Objects.equals(id, subtask.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, status, id);
    }

    @Override
    public String toString() {
        Class<? extends Subtask> c = getClass();
        return c.getSimpleName() + "{" +
                "Id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", epicId=" + epicId +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", duration='" + durationConverter(duration) +
                '}';
    }
}
