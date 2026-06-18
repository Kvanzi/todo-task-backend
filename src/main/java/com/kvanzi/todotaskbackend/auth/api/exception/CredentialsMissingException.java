package com.kvanzi.todotaskbackend.auth.api.exception;

public class CredentialsMissingException extends RuntimeException {
    public CredentialsMissingException(String message) {
        super(message);
    }
}
