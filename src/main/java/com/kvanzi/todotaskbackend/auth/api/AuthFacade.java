package com.kvanzi.todotaskbackend.auth.api;

import com.kvanzi.todotaskbackend.auth.api.dto.JwtSummary;
import com.kvanzi.todotaskbackend.auth.internal.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthFacade {
    private final JwtService jwtService;

     public @NonNull JwtSummary extractJwtSummary(@NonNull String token) {
        return jwtService.extractJwtSummary(token);
     }
}
