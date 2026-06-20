package com.kvanzi.todotaskbackend.app.security;

import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetails;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    @EqualsAndHashCode.Include
    @NonNull
    private final String token;

    @Nullable
    private final IdentifiableUserDetails userDetails;

    public JwtAuthenticationToken(@NonNull String token, @NonNull IdentifiableUserDetails userDetails) {
        super(userDetails.getAuthorities());
        this.token = token;
        this.userDetails = userDetails;
        setAuthenticated(true);
    }

    public JwtAuthenticationToken(@NonNull String token) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.token = token;
        this.userDetails = null;
        setAuthenticated(false);
    }

    @Override
    public @Nullable Object getCredentials() {
        return this.token;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return this.userDetails;
    }
}
