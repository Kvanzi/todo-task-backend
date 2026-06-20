package com.kvanzi.todotaskbackend.app.security;

import com.kvanzi.todotaskbackend.auth.api.AuthFacade;
import com.kvanzi.todotaskbackend.auth.api.dto.JwtSummary;
import com.kvanzi.todotaskbackend.auth.api.dto.JwtTokenType;
import com.kvanzi.todotaskbackend.auth.api.exception.InvalidJwtTokenException;
import com.kvanzi.todotaskbackend.auth.api.exception.InvalidJwtTokenTypeException;
import com.kvanzi.todotaskbackend.auth.api.exception.JwtTokenExpiredException;
import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetails;
import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final AuthFacade authFacade;
    private final IdentifiableUserDetailsService userDetailsService;

    @Override
    public @Nullable Authentication authenticate(@NonNull Authentication authentication)
        throws AuthenticationException {
        if (authentication.isAuthenticated()) {
            return authentication;
        }
        JwtAuthenticationToken unauthenticatedToken = (JwtAuthenticationToken) authentication;
        String tokenString = unauthenticatedToken.getToken();

        JwtSummary tokenSummary = authFacade.extractJwtSummary(tokenString);
        if (tokenSummary.getTokenType() != JwtTokenType.ACCESS) {
            throw new InvalidJwtTokenTypeException("You cannot use this token type for access this resource.");
        }

        if (tokenSummary.isExpired()) {
            throw new JwtTokenExpiredException("Token expired. Re login.");
        }

        IdentifiableUserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserById(tokenSummary.getUserId());
        } catch (UsernameNotFoundException e) {
            throw new InvalidJwtTokenException("Invalid refresh token. Try to re login.");
        }

        JwtAuthenticationToken authenticatedToken = new JwtAuthenticationToken(tokenString, userDetails);

        if (userDetails.getLastPasswordChangedAt() == null) {
            return authenticatedToken;
        }

        if (tokenSummary.passwordChangedAfterTokenIssued(userDetails.getLastPasswordChangedAt())) {
            throw new JwtTokenExpiredException("Token expired. Re login.");
        }

        return authenticatedToken;
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
