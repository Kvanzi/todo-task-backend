package com.kvanzi.todotaskbackend.app.security;

import com.kvanzi.todotaskbackend.auth.api.exception.JwtTokenException;
import com.kvanzi.todotaskbackend.shared.api.HttpApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    private final JsonMapper mapper;

    @Override
    public void commence(@NonNull HttpServletRequest request, HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        // same as cache-control but for HTTP/1.0 when cache-control for HTTP/1.1
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");

        String message = "Full authentication is required to access this resource.";
        boolean expected =
            authException instanceof JwtTokenException || authException instanceof InsufficientAuthenticationException;

        UUID errorId = null;
        if (expected) {
            message = authException.getMessage();
        } else {
            errorId = UUID.randomUUID();
            log.error("[{}] Unexpected error occurred", errorId, authException);
        }

        response.getWriter().write(mapper.writeValueAsString(
            HttpApiResponse.of(message, null, errorId)
        ));
        response.getWriter().flush();
    }
}
