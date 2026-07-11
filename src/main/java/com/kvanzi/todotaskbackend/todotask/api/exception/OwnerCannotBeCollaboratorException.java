package com.kvanzi.todotaskbackend.todotask.api.exception;

public class OwnerCannotBeCollaboratorException extends RuntimeException {
    public OwnerCannotBeCollaboratorException(String message) {
        super(message);
    }
}
