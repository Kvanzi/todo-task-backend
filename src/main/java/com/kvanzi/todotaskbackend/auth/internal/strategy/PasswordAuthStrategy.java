package com.kvanzi.todotaskbackend.auth.internal.strategy;

import com.kvanzi.todotaskbackend.auth.api.exception.CredentialsMissingException;
import com.kvanzi.todotaskbackend.auth.api.exception.InvalidCredentialsException;
import com.kvanzi.todotaskbackend.auth.internal.dto.CreateTokensRequest;
import com.kvanzi.todotaskbackend.auth.internal.dto.CreateTokensResponse;
import com.kvanzi.todotaskbackend.auth.internal.dto.GrantType;
import com.kvanzi.todotaskbackend.auth.internal.service.JwtService;
import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PasswordAuthStrategy implements AuthStrategy {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Override
    public GrantType getSupportedGrantType() {
        return GrantType.PASSWORD;
    }

    @Override
    @Transactional
    public CreateTokensResponse authenticate(CreateTokensRequest request, String refreshToken) {
        if (!request.isEmailAndPasswordProvided()) {
            throw new CredentialsMissingException("Email or password fields are missing.");
        }

        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            ));

            if (auth.getPrincipal() instanceof IdentifiableUserDetails userDetails) {
                var userId = userDetails.getId();
                return CreateTokensResponse.builder()
                    .accessToken(jwtService.generateAccessToken(userId))
                    .refreshToken(jwtService.generateRefreshToken(userId))
                    .build();
            }

            throw new IllegalStateException("Authentication manager returned null principal in authentication.");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Email or password are incorrect.");
        }
    }
}
