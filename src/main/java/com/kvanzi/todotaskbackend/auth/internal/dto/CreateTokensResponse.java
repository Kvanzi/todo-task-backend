package com.kvanzi.todotaskbackend.auth.internal.dto;

import lombok.Builder;
import lombok.Value;
import org.jspecify.annotations.NonNull;

@Value
@Builder
public class CreateTokensResponse {
    @NonNull
    String accessToken;

    @NonNull
    String refreshToken;
}
