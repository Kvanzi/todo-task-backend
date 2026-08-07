package com.kvanzi.todotaskbackend.user.api.exception;

public class LastAdminCannotBeDeletedException extends RuntimeException {
    public LastAdminCannotBeDeletedException(String message) {
        super(message);
    }
}
