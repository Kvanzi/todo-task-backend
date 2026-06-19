package com.kvanzi.todotaskbackend.auth.api.exception;

public class InvalidJwtTokenException extends JwtTokenException {
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
