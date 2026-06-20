package com.kvanzi.todotaskbackend.auth.internal.entity;

import com.kvanzi.todotaskbackend.auth.api.dto.JwtTokenType;
import com.kvanzi.todotaskbackend.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.NonNull;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "jwt_tokens",
    indexes = {
        @Index(name = "idx_jwt_token_user_id", columnList = "user_id")
    }
)
@SuppressWarnings("java:S2160")
public class JwtToken extends BaseEntity {
    @Builder.Default
    boolean revoked = false;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private JwtTokenType tokenType;

    @NonNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @NonNull
    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @NonNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
