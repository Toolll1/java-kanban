package taskManager.manager.managerForHistory;

import taskManager.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> tasksHistory = new HashMap<>();
    private final CustomLinkedList<Task> customHistoryList = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        customHistoryList.linkLast(task);
        tasksHistory.put(task.getId(), customHistoryList.tail.getPrev());
    }

    @Override
    public void remove(int id) {
        if (tasksHistory.containsKey(id)) {
            customHistoryList.removeNode(tasksHistory.get(id));
            tasksHistory.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return customHistoryList.getTasks();
    }

    public static class CustomLinkedList<T>{
        private Node<T> head;
        private Node<T> tail;

        public void linkLast(T element) {
            if (head == null) {

                Node<T> currentNode = new Node<>(tail, element, null);
                head = currentNode;
                tail = new Node<>(currentNode, null, null);
                return;
            }
            Node<T> currentNode = tail;
            currentNode.setData(element);
            tail = new Node<>(currentNode, null, null);
            currentNode.getPrev().setNext(currentNode);
            currentNode.setNext(tail);
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

        public void removeNode(Node<T> node) {
            if (node == null) {
                return;
            }

            final Node<T> prev = node.getPrev();
            final Node<T> next = node.getNext();

            if (prev == null) {
                head = next;
            } else {
                prev.setNext(next);
                node.setPrev(null);
            }
            if (next == null) {
                tail = prev;
            } else {
                next.setPrev(prev);
                node.setNext(null);
            }
            node.setData(null);
        }
    }
}
