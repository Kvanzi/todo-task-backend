package com.kvanzi.todotaskbackend.auth.internal.strategy;

import com.kvanzi.todotaskbackend.auth.internal.dto.CreateTokensRequest;
import com.kvanzi.todotaskbackend.auth.internal.dto.CreateTokensResponse;
import com.kvanzi.todotaskbackend.auth.internal.dto.GrantType;

public interface AuthStrategy {
    GrantType getSupportedGrantType();
    CreateTokensResponse authenticate(CreateTokensRequest request);
}
