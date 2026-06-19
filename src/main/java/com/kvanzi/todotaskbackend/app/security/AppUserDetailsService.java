package com.kvanzi.todotaskbackend.app.security;

import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetails;
import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetailsService;
import com.kvanzi.todotaskbackend.user.api.UserFacade;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements IdentifiableUserDetailsService {
    private final UserFacade userFacade;

    /**
     * Note: {@code username} is actually an email.
     *
     * @param username the user's email address
     */
    @Override
    public @NonNull IdentifiableUserDetails loadUserByUsername(@NonNull String username)
        throws UsernameNotFoundException {
        return userFacade.findSecurityUserDetailsByEmailIgnoreCase(username)
            .orElseThrow(() -> new UsernameNotFoundException("User with email '%s' not found".formatted(username)));
    }

    @Override
    public @NonNull IdentifiableUserDetails loadUserById(@NonNull UUID id) {
        return userFacade.findSecurityUserDetailsById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User with id '%s' not found".formatted(id)));
    }
}
