package ru.practicum.interaction.error.exception;

public class AccessException extends RuntimeException {
    public AccessException(String message) {
        super(message);
    }
}