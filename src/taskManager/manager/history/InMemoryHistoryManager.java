package taskManager.manager.history;

import taskManager.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> customHistoryList = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        customHistoryList.linkLast(task);
    }

    @Override
    public void remove(int id) {
        customHistoryList.removeNode(id);
    }

    @Override
    public List<Task> getHistory() {
        return customHistoryList.getTasks();
    }

    public static class CustomLinkedList<T extends Task> {
        private Node<T> head;
        private Node<T> tail;
        private final Map<Integer, Node<Task>> tasksHistory = new HashMap<>();

        public void linkLast(T element) {

            final Node <T> oldTail = tail;
            final Node <T> currentNode = new Node<T>(oldTail, element, null);
            tail = currentNode;
            if (oldTail == null) {
                head = currentNode;
            } else {
                oldTail.setNext(currentNode);
            }
            tasksHistory.put(element.getId(), (Node<Task>) currentNode);
        }

        public List<T> getTasks() {
            List<T> tasksList = new ArrayList<>();
            Node<T> node = head;

            while (node != null) {
                tasksList.add(node.getData());
                node = node.getNext();
            }
            return tasksList;
        }

        public void removeNode(int id) {
            if (tasksHistory.containsKey(id)) {
                Node<Task> node = tasksHistory.get(id);

                if (node == null) {
                    return;
                }

                final Node<Task> prev = node.getPrev();
                final Node<Task> next = node.getNext();

                if (prev == null) {
                    head = (Node<T>) next;
                } else {
                    prev.setNext(next);
                }
                if (next == null) {
                    tail = (Node<T>) prev;
                } else {
                    next.setPrev(prev);
                }
                tasksHistory.remove(id);
            }
        }
    }
}