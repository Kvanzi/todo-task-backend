package com.kvanzi.todotaskbackend.todotask.api.exception;

public class ToDoTaskNotFoundException extends RuntimeException {
    public ToDoTaskNotFoundException(String message) {
        super(message);
    }
}
