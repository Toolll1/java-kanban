package taskManager.task;

import taskManager.status.Status;

import java.util.Objects;

public class Subtask extends Task {

    protected Integer epicId;

    public Subtask(Status status, String title, String description, int epicId) {
        super(status, title, description, epicId);
        this.epicId = epicId;
    }

    public Integer getEpicId(){
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
        Subtask subTask = (Subtask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "Id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", epicId=" + epicId +
                '}';
    }
}
