package task;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList <Integer> subtaskIds = new ArrayList<>();

    public Epic(Status status, String title, String description) {
        super(status, title, description);
    }

    public void addSubTaskId(int id) {
        subtaskIds.add(id);
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
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}



