package com.kvanzi.todotaskbackend.auth.internal.controller;

import com.kvanzi.todotaskbackend.auth.internal.dto.CreateTokensRequest;
import com.kvanzi.todotaskbackend.auth.internal.dto.CreateTokensResponse;
import com.kvanzi.todotaskbackend.auth.internal.enumeration.JwtTokenType;
import com.kvanzi.todotaskbackend.auth.internal.properties.JwtProperties;
import com.kvanzi.todotaskbackend.auth.internal.service.AuthService;
import com.kvanzi.todotaskbackend.auth.internal.service.JwtService;
import com.kvanzi.todotaskbackend.shared.api.HttpApiResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @PostMapping("/tokens")
    public ResponseEntity<@NonNull HttpApiResponse<Void, Void>> createAuthTokens(
        @Valid @RequestBody CreateTokensRequest request) {
        CreateTokensResponse tokensResponse = authService.processCreationTokensRequest(request);
        ResponseCookie refreshCookie = buildRefreshResponseCookie(tokensResponse.getRefreshToken());
        ResponseCookie accessCookie = buildAccessResponseCookie(tokensResponse.getAccessToken());

        return HttpApiResponse.<Void>ok()
            .addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString(), accessCookie.toString())
            .build();
    }

    @DeleteMapping("/tokens/current")
    public ResponseEntity<@NonNull HttpApiResponse<Void, Void>> revokeAuthTokens(
        @CookieValue(name = "refresh", required = false) @Nullable String refreshToken
    ) {
        jwtService.revokeTokensGracefully(refreshToken);
        return HttpApiResponse.<Void>status(HttpStatus.NO_CONTENT)
            .addHeader(HttpHeaders.SET_COOKIE,
                buildEmptyRefreshCookie().toString(),
                buildEmptyAccessCookie().toString()
            )
            .build();
    }

    private ResponseCookie buildAccessResponseCookie(String token) {
        Duration maxAge =
            Duration.of(jwtProperties.getAccess().getDuration(), jwtProperties.getAccess().getDurationUnit());

        return buildTokenResponseCookie(JwtTokenType.ACCESS, token, maxAge);
    }

    private ResponseCookie buildEmptyRefreshCookie() {
        return buildTokenResponseCookie(JwtTokenType.REFRESH, StringUtils.EMPTY, Duration.ZERO);
    }

    private ResponseCookie buildEmptyAccessCookie() {
        return buildTokenResponseCookie(JwtTokenType.ACCESS, StringUtils.EMPTY, Duration.ZERO);
    }

    private ResponseCookie buildRefreshResponseCookie(String token) {
        Duration maxAge =
            Duration.of(jwtProperties.getRefresh().getDuration(), jwtProperties.getRefresh().getDurationUnit());

        return buildTokenResponseCookie(JwtTokenType.REFRESH, token, maxAge);
    }

    private ResponseCookie buildTokenResponseCookie(JwtTokenType tokenType, String token, Duration maxAge) {
        return ResponseCookie.from(tokenType == JwtTokenType.ACCESS ? "access" : "refresh", token)
            .maxAge(maxAge)
            .httpOnly(jwtProperties.getCookie().isHttpOnly())
            .sameSite(jwtProperties.getCookie().getSameSite().attributeValue())
            .domain(jwtProperties.getCookie().getDomain())
            .path("/")
            .build();
    }
}
