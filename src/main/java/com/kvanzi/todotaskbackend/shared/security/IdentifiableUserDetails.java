package com.kvanzi.todotaskbackend.shared.security;

import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

public interface IdentifiableUserDetails extends UserDetails {
    @NonNull UUID getId();

    @Nullable Instant getLastPasswordChangedAt();
}
