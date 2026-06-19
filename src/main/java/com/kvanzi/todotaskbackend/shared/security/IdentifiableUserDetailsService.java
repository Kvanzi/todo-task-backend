package com.kvanzi.todotaskbackend.shared.security;

import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IdentifiableUserDetailsService extends UserDetailsService {
    @NonNull IdentifiableUserDetails loadUserById(@NonNull UUID id);
}
