package com.kvanzi.todotaskbackend.shared.security;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;

@Builder
@Value
@SuppressWarnings("java:S1948")
public class IdentifiableUserDetailsImpl implements IdentifiableUserDetails {
    UUID id;

    @Builder.Default
    Collection<? extends GrantedAuthority> authorities = new HashSet<>();
    String password;
    String username;
    Instant lastPasswordChangedAt;

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(this.authorities);
    }
}
