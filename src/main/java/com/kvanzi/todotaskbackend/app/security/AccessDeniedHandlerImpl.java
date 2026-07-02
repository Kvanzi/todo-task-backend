package com.kvanzi.todotaskbackend.app.security;

import com.kvanzi.todotaskbackend.shared.api.HttpApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    private final JsonMapper mapper;

    @Override
    public void handle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                       @NonNull AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        // same as cache-control but for HTTP/1.0 when cache-control for HTTP/1.1
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");

        String message = "Access denied";
        if (accessDeniedException instanceof CsrfException csrfException) {
            message = csrfException.getMessage();
        }

        response.getWriter().write(mapper.writeValueAsString(
            HttpApiResponse.of(message, null, null)
        ));
        response.getWriter().flush();
    }
}
