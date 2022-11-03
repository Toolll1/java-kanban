package task;

import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected Status status;
    /* не понимаю, что нужно переделать, касательно этого комментария:
    "у статуса 3 возможных значения лучше сделать Enum с этими значениями и хранить тут значение из него".
    И так же идет ссылка на Enum
     */
    protected Integer id;

    public Task(Status status, String title, String description) {
        this.title = title;
        this.description = description;
        this.status=status;
    }

    public Task(Status status, String title, String description, int Id) {
        this.title = title;
        this.description = description;
        this.status=status;
        this.id = Id;
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

    public int getId() {
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
        return id == task.id &&
                Objects.equals(title, task.title) &&
                status == task.status &&
                Objects.equals(description, task.description);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", deskription='" + description + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, status, description);
    }

}
