package taskManager.manager.managerForHistory;

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
        customHistoryList.linkLast(task, customHistoryList, task);
    }

    @Override
    public void remove(int id) {
        customHistoryList.removeNode(id, customHistoryList);
    }

    @Override
    public List<Task> getHistory() {
        return customHistoryList.getTasks();
    }

    public static class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;
        private static final Map<Integer, Node<Task>> tasksHistory = new HashMap<>();

        public void linkLast(T element, CustomLinkedList<Task> customHistoryList, Task task) {
            if (head == null) {
                head = new Node<T>(element);
                tasksHistory.put(task.getId(), (Node<Task>) head);
                return;
            }
            Node<T> currentNode = head;
            while (currentNode.getNext() != null) {
                currentNode = currentNode.getNext();
            }
            currentNode.setNext(new Node<T>(currentNode, element, null));

            tasksHistory.put(task.getId(), (Node<Task>) currentNode);
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

        public void removeNode(int id, CustomLinkedList<T> customHistoryList) {

            if (tasksHistory.containsKey(id)) {
                Node<T> node = (Node<T>) tasksHistory.get(id);

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

                tasksHistory.remove(id);
            }
        }
    }
}
