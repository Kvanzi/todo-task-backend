package com.kvanzi.todotaskbackend.app.security;

import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetails;
import com.kvanzi.todotaskbackend.user.api.UserFacade;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
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
}
