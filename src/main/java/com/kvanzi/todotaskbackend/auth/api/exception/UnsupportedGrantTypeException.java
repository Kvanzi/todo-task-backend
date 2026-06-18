package com.kvanzi.todotaskbackend.auth.api.exception;

import com.kvanzi.todotaskbackend.auth.internal.dto.GrantType;

public class UnsupportedGrantTypeException extends RuntimeException {
    public UnsupportedGrantTypeException(GrantType grantType) {
        super("Grant type '%s' is not supported.".formatted(grantType));
    }
}
