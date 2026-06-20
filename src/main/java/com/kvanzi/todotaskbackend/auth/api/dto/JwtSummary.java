package com.kvanzi.todotaskbackend.auth.api.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Value;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Value
public class JwtSummary {
    @Nullable
    UUID tokenId;
    @NonNull
    UUID userId;
    @NonNull
    JwtTokenType tokenType;
    @NonNull
    Instant issuedAt;
    boolean expired;

    public boolean passwordChangedAfterTokenIssued(@Nullable Instant lastPasswordChangedAt) {
        return lastPasswordChangedAt != null && lastPasswordChangedAt.isAfter(this.getIssuedAt());
    }
}
