package taskManager.task;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    protected Integer epicId;

    public Subtask(Status status, String title, String description, int epicId, LocalDateTime startTime, int duration) {
        super(status, title, description, epicId, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Status status, String title, String description, int Id, Integer epicId, LocalDateTime startTime, int duration) {
        super(status, title, description, Id, startTime, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
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
                ", startTime='" + startTime.format(formatter) + '\'' +
                ", endTime='" + getEndTime().format(formatter) + '\'' +
                ", duration='" + super.durationConverter(duration) +
                '}';
    }
}
