package com.kvanzi.todotaskbackend.app.security;

import com.kvanzi.todotaskbackend.auth.api.exception.JwtTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authManager;
    private final AuthenticationEntryPoint authEntryPoint;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = extractAccessToken(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var authenticationToken = new JwtAuthenticationToken(accessToken);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            authentication = authManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtTokenException e) {
            authEntryPoint.commence(request, response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private @Nullable String extractAccessToken(@NonNull HttpServletRequest request) {
        return Stream.ofNullable(request.getCookies())
            .flatMap(Arrays::stream)
            .filter(cookie -> "access".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
    }
}
