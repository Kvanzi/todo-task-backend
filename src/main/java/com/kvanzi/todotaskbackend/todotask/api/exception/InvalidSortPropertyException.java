package com.kvanzi.todotaskbackend.todotask.api.exception;

public class InvalidSortPropertyException extends RuntimeException {
    public InvalidSortPropertyException(String property) {
        super("Invalid sort field: " + property);
    }
}
