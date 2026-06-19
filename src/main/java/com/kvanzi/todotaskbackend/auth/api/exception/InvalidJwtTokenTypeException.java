package com.kvanzi.todotaskbackend.auth.api.exception;

public class InvalidJwtTokenTypeException extends JwtTokenException {
    public InvalidJwtTokenTypeException(String message) {
        super(message);
    }
}
