package ru.yandex.practicum.vyunnikov.taskManager.exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final String message) {
        super(message);
    }
}
