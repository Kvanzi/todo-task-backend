package com.kvanzi.todotaskbackend.app.handler;

import com.kvanzi.todotaskbackend.auth.api.exception.CredentialsMissingException;
import com.kvanzi.todotaskbackend.auth.api.exception.InvalidCredentialsException;
import com.kvanzi.todotaskbackend.auth.api.exception.MissingRefreshTokenException;
import com.kvanzi.todotaskbackend.shared.api.HttpApiResponse;
import com.kvanzi.todotaskbackend.user.api.exception.EmailTakenException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public @NonNull ResponseEntity<@NonNull HttpApiResponse<Void, UUID>> handleInternalError(Exception e) {
        UUID errorId = UUID.randomUUID();
        log.error("[{}] Unexpected error occurred", errorId, e);
        return HttpApiResponse.<Void>internalServerError()
            .message("Unexpected error occurred. Please try again.")
            .meta(errorId)
            .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public @NonNull ResponseEntity<
        @NonNull HttpApiResponse<@NonNull Map<@NonNull String, @NonNull List<@NonNull String>>, Void>
        > handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<@NonNull String, @NonNull List<@NonNull String>> errors = e.getFieldErrors().stream()
            .filter(fieldError -> Objects.nonNull(fieldError.getDefaultMessage()))
            .collect(Collectors.groupingBy(
                FieldError::getField,
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
            ));
        return HttpApiResponse.<Map<@NonNull String, @NonNull List<@NonNull String>>>badRequest()
            .message("Validation exception.")
            .data(errors)
            .build();
    }

    @SuppressWarnings("NullableProblems")
    @ExceptionHandler(HandlerMethodValidationException.class)
    public @NonNull ResponseEntity<
        @NonNull HttpApiResponse<@NonNull Map<@NonNull String, @NonNull List<@NonNull String>>, Void>
        > handleHandlerMethodValidationException(
        HandlerMethodValidationException e) {
        Map<@NonNull String, @NonNull List<@NonNull String>> errors = e.getParameterValidationResults().stream()
            .filter(result -> result.getMethodParameter().getParameterName() != null)
            .collect(Collectors.toMap(
                result -> Objects.requireNonNull(result.getMethodParameter().getParameterName()),
                result -> result.getResolvableErrors().stream()
                    .flatMap(error -> Optional.ofNullable(error.getDefaultMessage()).stream())
                    .toList()
            ));

        return HttpApiResponse.<Map<@NonNull String, @NonNull List<@NonNull String>>>badRequest()
            .message("Validation exception.")
            .data(errors)
            .build();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public @NonNull ResponseEntity<@NonNull HttpApiResponse<Void, Void>> handleNoResourceFoundException() {
        return HttpApiResponse.<Void>notFound()
            .message("Resource not found.")
            .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public @NonNull ResponseEntity<@NonNull HttpApiResponse<Void, Void>> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException e) {
        return HttpApiResponse.<Void>badRequest()
            .message("Invalid type for parameter '%s'.".formatted(e.getName()))
            .build();
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<@NonNull HttpApiResponse<Void, Void>> handleAuthorizationDeniedException() {
        return HttpApiResponse.<Void>status(HttpStatus.FORBIDDEN)
            .message("Access denied.")
            .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<@NonNull HttpApiResponse<Void, Void>> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException e) {
        String message = "Malformed JSON request. Please check the request body structure and data types";

        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            String fieldName = invalidFormatException.getPath().stream()
                .map(JacksonException.Reference::getPropertyName)
                .reduce((f1, f2) -> f1 + "." + f2)
                .orElse("unknown_field");

            message = String.format("Invalid value provided for field '%s'", fieldName);
        }

        return HttpApiResponse.<Void>badRequest()
            .message(message)
            .build();
    }

    @ExceptionHandler(EmailTakenException.class)
    public ResponseEntity<@NonNull HttpApiResponse<Void, Void>> handleEmailTakenException(EmailTakenException e) {
        return HttpApiResponse.<Void>status(HttpStatus.CONFLICT)
            .message(e.getMessage())
            .build();
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<@NonNull HttpApiResponse<Void, Void>> handleInvalidCredentialsException(
        InvalidCredentialsException e) {
        return HttpApiResponse.<Void>unauthorized()
            .message(e.getMessage())
            .build();
    }

    @ExceptionHandler(CredentialsMissingException.class)
    public ResponseEntity<@NonNull HttpApiResponse<Void, Void>> handleCredentialsMissingException(
        CredentialsMissingException e) {
        return HttpApiResponse.<Void>status(HttpStatus.BAD_REQUEST)
            .message(e.getMessage())
            .build();
    }

    @ExceptionHandler(MissingRefreshTokenException.class)
    public ResponseEntity<@NonNull HttpApiResponse<Void, Void>> handleMissingRefreshTokenException(MissingRefreshTokenException e) {
        return HttpApiResponse.<Void>unauthorized()
            .message(e.getMessage())
            .build();
    }
}
