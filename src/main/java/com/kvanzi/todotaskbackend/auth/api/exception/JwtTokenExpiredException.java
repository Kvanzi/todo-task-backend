package com.kvanzi.todotaskbackend.auth.api.exception;

public class JwtTokenExpiredException extends JwtTokenException {
    public JwtTokenExpiredException(String message) {
        super(message);
    }
}
