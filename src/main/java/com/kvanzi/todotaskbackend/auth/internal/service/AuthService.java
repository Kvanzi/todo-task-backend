package com.kvanzi.todotaskbackend.auth.internal.service;

import com.kvanzi.todotaskbackend.auth.api.exception.UnsupportedGrantTypeException;
import com.kvanzi.todotaskbackend.auth.internal.dto.CreateTokensRequest;
import com.kvanzi.todotaskbackend.auth.internal.dto.CreateTokensResponse;
import com.kvanzi.todotaskbackend.auth.internal.dto.GrantType;
import com.kvanzi.todotaskbackend.auth.internal.strategy.AuthStrategy;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final Map<@NonNull GrantType, @NonNull AuthStrategy> authStrategies;

    public AuthService(List<AuthStrategy> authStrategies) {
        this.authStrategies = authStrategies.stream()
            .collect(Collectors.toUnmodifiableMap(AuthStrategy::getSupportedGrantType, Function.identity()));
    }

    public @NonNull CreateTokensResponse processCreationTokensRequest(@NonNull CreateTokensRequest request,
                                                                      @Nullable String refreshToken) {
        var grantType = request.getGrantType();
        return Optional.ofNullable(authStrategies.get(grantType))
            .orElseThrow(() -> new UnsupportedGrantTypeException(grantType))
            .authenticate(request, refreshToken);
    }
}
