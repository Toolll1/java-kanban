package ru.yandex.practicum.vyunnikov.taskManager.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(Status status, String title, String description, LocalDateTime startTime, Duration duration) {
        super(status, title, description, startTime, duration);
        this.endTime = super.getEndTime();
    }

    public Epic(Status status, String title, String description, int Id, LocalDateTime startTime, Duration duration) {
        super(status, title, description, Id, startTime, duration);
        this.endTime = super.getEndTime();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void addSubTaskId(int id) {
        if (subtaskIds == null) subtaskIds = new ArrayList<>();
        if (!subtaskIds.contains(id)) subtaskIds.add(id);
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void removeSubTaskId(int subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(title, epic.title) && Objects.equals(description, epic.description)
                && status == epic.status && Objects.equals(id, epic.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, status, id);
    }

    @Override
    public String toString() {
        Class<? extends Epic> c = getClass();
        return c.getSimpleName() + "{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", duration='" + durationConverter(duration) +
                '}';
    }
}



