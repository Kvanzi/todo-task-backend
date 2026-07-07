package com.kvanzi.todotaskbackend.auth.internal.strategy;

import com.kvanzi.todotaskbackend.auth.api.exception.InvalidJwtTokenException;
import com.kvanzi.todotaskbackend.auth.api.exception.InvalidJwtTokenTypeException;
import com.kvanzi.todotaskbackend.auth.api.exception.JwtTokenExpiredException;
import com.kvanzi.todotaskbackend.auth.api.exception.MissingRefreshTokenException;
import com.kvanzi.todotaskbackend.auth.internal.dto.CreateTokensRequest;
import com.kvanzi.todotaskbackend.auth.internal.dto.CreateTokensResponse;
import com.kvanzi.todotaskbackend.auth.internal.dto.GrantType;
import com.kvanzi.todotaskbackend.auth.api.dto.JwtSummary;
import com.kvanzi.todotaskbackend.auth.api.dto.JwtTokenType;
import com.kvanzi.todotaskbackend.auth.internal.service.JwtService;
import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetails;
import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetailsService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RefreshTokenAuthStrategy implements AuthStrategy {
    private final JwtService jwtService;
    private final IdentifiableUserDetailsService userDetailsService;

    @Override
    public GrantType getSupportedGrantType() {
        return GrantType.REFRESH_TOKEN;
    }

    /**
     * Creates access & refresh tokens by refreshToken.
     *
     * @throws MissingRefreshTokenException when is refresh token is missing.
     * @throws InvalidJwtTokenException     when trying to extract token and token is malformed or have a bad structure
     * @throws InvalidJwtTokenTypeException when token type is not REFRESH
     * @throws JwtTokenExpiredException     when token expired
     */
    @Transactional
    @Override
    public @NonNull CreateTokensResponse authenticate(@NonNull CreateTokensRequest request,
                                                      @Nullable String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new MissingRefreshTokenException("Refresh token cannot be null or blank.");
        }

        JwtSummary tokenSummary;
        try {
            tokenSummary = jwtService.extractJwtSummary(refreshToken);
        } catch (SignatureException | UnsupportedJwtException | MalformedJwtException e) {
            throw new InvalidJwtTokenException("Invalid token. Try to re login.");
        }

        UUID tokenId = tokenSummary.getTokenId();

        if (tokenSummary.isExpired()) {
            throw new JwtTokenExpiredException("Token expired. Re login.");
        }

        if (tokenSummary.getTokenType() != JwtTokenType.REFRESH) {
            throw new InvalidJwtTokenTypeException("You cannot use this type of token to refresh access.");
        }

        if (tokenId == null) {
            throw new InvalidJwtTokenException("Invalid refresh token. Try to re login.");
        }

        if (jwtService.isTokenRevoked(tokenId)) {
            throw new InvalidJwtTokenException("Invalid refresh token. Try to re login.");
        }

        IdentifiableUserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserById(tokenSummary.getUserId());
        } catch (UsernameNotFoundException e) {
            jwtService.revokeToken(tokenId);
            throw new InvalidJwtTokenException("Invalid refresh token. Try to re login.");
        }

        if (tokenSummary.passwordChangedAfterTokenIssued(userDetails.getLastPasswordChangedAt())) {
            throw new InvalidJwtTokenException("Invalid refresh token. Try to re login.");
        }

        jwtService.revokeToken(tokenId);

        UUID userId = userDetails.getId();
        return CreateTokensResponse.builder()
            .accessToken(jwtService.generateAccessToken(userId))
            .refreshToken(jwtService.generateRefreshToken(userId))
            .build();
    }
}
