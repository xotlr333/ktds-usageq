package com.telco.management.exception;

public class QueueException extends RuntimeException {
    public QueueException(String message) {
        super(message);
    }

    public QueueException(String message, Throwable cause) {
        super(message, cause);
    }
}