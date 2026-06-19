package com.kvanzi.todotaskbackend.auth.internal.service;

import com.kvanzi.todotaskbackend.auth.internal.dto.JwtSummary;
import com.kvanzi.todotaskbackend.auth.internal.entity.JwtToken;
import com.kvanzi.todotaskbackend.auth.internal.enumeration.JwtTokenType;
import com.kvanzi.todotaskbackend.auth.internal.properties.JwtProperties;
import com.kvanzi.todotaskbackend.auth.internal.repository.JwtTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class JwtService {
    private static final String TOKEN_TYPE_KEY = "token_type";
    private final JwtProperties jwtProperties;
    private final JwtTokenRepository tokenRepository;
    private final JwtParser verifiedJwtParser;

    public JwtService(JwtProperties jwtProperties, JwtTokenRepository tokenRepository) {
        this.jwtProperties = jwtProperties;
        this.tokenRepository = tokenRepository;
        this.verifiedJwtParser = Jwts.parser()
            .verifyWith(jwtProperties.getSigningKey())
            .build();
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        executeTokenClearance();
    }

    public boolean isTokenRevoked(@NonNull UUID tokenId) {
        return tokenRepository.existsByIdAndRevokedIsTrue(tokenId);
    }

    public void revokeToken(@NonNull UUID tokenId) {
        tokenRepository.revokeToken(tokenId);
    }

    public void revokeTokensGracefully(String... tokens) {
        if (tokens == null || tokens.length == 0) {
            return;
        }

        tokenRepository.revokeTokens(Stream.of(tokens)
            .filter(Objects::nonNull)
            .map(token -> {
                try {
                    return Optional.ofNullable(this.extractClaims(token));
                } catch (Exception e) {
                    log.warn("Failed to extract claims for token: [{}]. Reason: {}",
                        maskToken(token), e.getMessage());
                    return Optional.<Claims>empty();
                }
            })
            .flatMap(Optional::stream)
            .map(claims -> {
                try {
                    return Optional.ofNullable(this.extractTokenId(claims));
                } catch (Exception e) {
                    log.warn("Failed to extract token id from claims. Reason: {}", e.getMessage());
                    return Optional.<UUID>empty();
                }
            })
            .flatMap(Optional::stream)
            .collect(Collectors.toSet())
        );
    }

    @Scheduled(cron = "0 0 * * * *")
    public void clearExpiredTokens() {
        executeTokenClearance();
    }

    public String generateRefreshToken(UUID userId) {
        return generateRefreshToken(userId, new HashMap<>());
    }

    private String generateRefreshToken(UUID userId, Map<String, Object> extraClaims) {
        Instant issuedAt = Instant.now();
        Instant expiresAt =
            issuedAt.plus(jwtProperties.getRefresh().getDuration(), jwtProperties.getRefresh().getDurationUnit());

        JwtToken tokenEntity = JwtToken.builder()
            .tokenType(JwtTokenType.REFRESH)
            .expiresAt(expiresAt)
            .issuedAt(issuedAt)
            .userId(userId)
            .build();
        tokenEntity = tokenRepository.save(tokenEntity);
        extraClaims.put(TOKEN_TYPE_KEY, JwtTokenType.REFRESH);

        return generateToken(
            issuedAt,
            expiresAt,
            userId,
            extraClaims,
            tokenEntity.getId()
        );
    }

    public String generateAccessToken(UUID userId) {
        return generateAccessToken(userId, new HashMap<>());
    }

    private String generateAccessToken(UUID userId, Map<String, Object> extraClaims) {
        Instant issuedAt = Instant.now();
        Instant expiresAt =
            issuedAt.plus(jwtProperties.getAccess().getDuration(), jwtProperties.getAccess().getDurationUnit());

        extraClaims.put(TOKEN_TYPE_KEY, JwtTokenType.ACCESS);

        return generateToken(
            issuedAt,
            expiresAt,
            userId,
            extraClaims,
            null
        );
    }

    public Claims extractClaims(String token) {
        try {
            return this.verifiedJwtParser
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public UUID extractUserId(Claims claims) {
        String sub = claims.getSubject();
        return UUID.fromString(sub);
    }

    public boolean isClaimsExpired(Claims claims) {
        return extractExpiresAt(claims).isBefore(Instant.now());
    }

    public JwtTokenType extractTokenType(Claims claims) {
        return JwtTokenType.valueOf(claims.get(TOKEN_TYPE_KEY, String.class));
    }

    public Instant extractIssuedAt(Claims claims) {
        return claims.getIssuedAt().toInstant();
    }

    public Instant extractExpiresAt(Claims claims) {
        return claims.getExpiration().toInstant();
    }

    public @Nullable UUID extractTokenId(Claims claims) {
        String id = claims.getId();
        return id != null ? UUID.fromString(id) : null;
    }

    public JwtSummary extractJwtSummary(String token) {
        Claims claims = extractClaims(token);

        return new JwtSummary(
            extractTokenId(claims),
            extractUserId(claims),
            extractTokenType(claims),
            extractIssuedAt(claims),
            isClaimsExpired(claims)
        );
    }

    private String generateToken(@Nullable Instant issuedAt, @Nullable Instant expiresAt, @NonNull UUID userId,
                                 @Nullable Map<String, Object> extraClaims, @Nullable UUID tokenId) {
        JwtBuilder builder = Jwts.builder();

        if (issuedAt != null) {
            builder.issuedAt(Date.from(issuedAt));
        }

        if (expiresAt != null) {
            builder.expiration(Date.from(expiresAt));
        }

        if (extraClaims != null && !extraClaims.isEmpty()) {
            builder.claims(extraClaims);
        }

        if (tokenId != null) {
            builder.id(tokenId.toString());
        }

        return builder
            .subject(userId.toString())
            .signWith(jwtProperties.getSigningKey())
            .compact();
    }

    private void executeTokenClearance() {
        tokenRepository.deleteAllExpired(Instant.now());
    }

    private String maskToken(String token) {
        if (token == null || token.length() <= 20) {
            return "***";
        }
        return token.substring(0, 10) + "..." + token.substring(token.length() - 10);
    }
}
