package taskManager.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected Status status;
    protected Integer id;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(Status status, String title, String description, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Status status, String title, String description, int Id, LocalDateTime startTime, Duration duration) {
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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
        return startTime.plusMinutes(duration.toMinutes());
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");

    public String durationConverter(Duration duration) {
        int MINUTES_PER_HOUR = 60;
        int MINUTES_PER_DAY = MINUTES_PER_HOUR * 24;

        if (duration.toMinutes() <= MINUTES_PER_HOUR) {
            return duration.toMinutes() + "мин.";
        } else if (duration.toMinutes() <= MINUTES_PER_DAY) {
            int hours = (int) (duration.toMinutes() / MINUTES_PER_HOUR);
            int minutes = (int) (duration.toMinutes() % MINUTES_PER_HOUR);
            return hours + "час. " + minutes + "мин.";
        } else {
            int days = (int) (duration.toMinutes() / MINUTES_PER_DAY);
            int hours = (int) (duration.toMinutes() % MINUTES_PER_DAY / MINUTES_PER_HOUR);
            int minutes = (int) (duration.toMinutes() % MINUTES_PER_DAY % MINUTES_PER_HOUR);

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
