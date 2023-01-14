package taskManager.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected Status status;
    protected Integer id;
    protected LocalDateTime startTime;
    protected int duration;

    public Task(Status status, String title, String description, LocalDateTime startTime, int duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Status status, String title, String description, int Id, LocalDateTime startTime, int duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = Id;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status && Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, status, id, startTime, duration);
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");

    public String durationConverter(int duration) {
        int MINUTES_PER_HOUR = 60;
        int MINUTES_PER_DAY = MINUTES_PER_HOUR * 24;

        if (duration <= MINUTES_PER_HOUR) {
            return duration + "мин.";
        } else if (duration <= MINUTES_PER_DAY) {
            int hours = duration / MINUTES_PER_HOUR;
            int minutes = duration % MINUTES_PER_HOUR;
            return hours + "час. " + minutes + "мин.";
        } else {
            int days = duration / MINUTES_PER_DAY;
            int hours = duration % MINUTES_PER_DAY / MINUTES_PER_HOUR;
            int minutes = duration % MINUTES_PER_DAY % MINUTES_PER_HOUR;

            return days + "дн. " + hours + "час. " + minutes + "мин.";
        }
    }

    @Override
    public String toString() {
        Class<? extends Task> c = getClass();
        return c.getSimpleName() + "{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", deskription='" + description + '\'' +
                ", startTime='" + startTime.format(formatter) + '\'' +
                ", endTime='" + getEndTime().format(formatter) + '\'' +
                ", duration='" + durationConverter(duration) +
                '}';
    }
}
