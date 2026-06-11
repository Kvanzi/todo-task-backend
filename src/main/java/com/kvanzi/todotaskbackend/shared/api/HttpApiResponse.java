package com.kvanzi.todotaskbackend.shared.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpApiResponse<T, M> {
    @NonNull
    private final String message;

    @Nullable
    private final T data;

    @Nullable
    private final M meta;

    @NonNull
    private final Instant timestamp;

    public static <T> Builder<T> status(@NonNull HttpStatus status) {
        return new Builder<>(status);
    }

    public static <T> Builder<T> ok() {
        return new Builder<>(HttpStatus.OK);
    }

    public static <T> Builder<T> created() {
        return new Builder<>(HttpStatus.CREATED);
    }

    public static <T> Builder<T> badRequest() {
        return new Builder<>(HttpStatus.BAD_REQUEST);
    }

    public static <T> Builder<T> notFound() {
        return new Builder<>(HttpStatus.NOT_FOUND);
    }

    public static <T> Builder<T> internalServerError() {
        return new Builder<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static final class Builder<T> {
        private final HttpStatus status;
        private final Instant timestamp = Instant.now();
        private String message;
        private T data;
        private final HttpHeaders headers = new HttpHeaders();

        private Builder(@NonNull HttpStatus status) {
            this.status = Objects.requireNonNull(status, "status cannot be null");
            this.message = status.getReasonPhrase();
        }

        public Builder<T> message(@NonNull String message) {
            this.message = Objects.requireNonNull(message, "message must not be null");
            return this;
        }

        public Builder<T> data(@Nullable T data) {
            this.data = data;
            return this;
        }

        public Builder<T> addHeader(@NonNull String name, @NonNull String... values) {
            this.headers.addAll(name, List.of(values));
            return this;
        }

        public Builder<T> location(@NonNull URI location) {
            this.headers.setLocation(location);
            return this;
        }

        public <M> BuilderWithMeta<T, M> meta(@NonNull M meta) {
            return new BuilderWithMeta<>(this, meta);
        }

        public ResponseEntity<@NonNull HttpApiResponse<T, Void>> build() {
            HttpApiResponse<T, Void> body = new HttpApiResponse<>(message, data, null, timestamp);
            return ResponseEntity.status(status)
                .headers(headers)
                .body(body);
        }
    }

    public static final class BuilderWithMeta<T, M> {
        private final Builder<T> base;
        private final M meta;

        private BuilderWithMeta(@NonNull Builder<T> base, @NonNull M meta) {
            this.base = base;
            this.meta = meta;
        }

        public BuilderWithMeta<T, M> message(@NonNull String message) {
            base.message(message);
            return this;
        }

        public BuilderWithMeta<T, M> data(@Nullable T data) {
            base.data(data);
            return this;
        }

        public BuilderWithMeta<T, M> addHeader(@NonNull String name, @NonNull String... values) {
            base.addHeader(name, values);
            return this;
        }

        public BuilderWithMeta<T, M> location(@NonNull URI location) {
            base.location(location);
            return this;
        }

        public ResponseEntity<@NonNull HttpApiResponse<T, M>> build() {
            HttpApiResponse<T, M> body = new HttpApiResponse<>(base.message, base.data, meta, base.timestamp);
            return ResponseEntity.status(base.status)
                .headers(base.headers)
                .body(body);
        }
    }
}