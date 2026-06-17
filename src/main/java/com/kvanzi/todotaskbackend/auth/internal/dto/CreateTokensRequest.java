package com.kvanzi.todotaskbackend.auth.internal.dto;

import com.kvanzi.todotaskbackend.shared.validation.UserEmail;
import com.kvanzi.todotaskbackend.shared.validation.UserPassword;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Value
public class CreateTokensRequest {
    @NotNull(message = "Grant type cannot be null.")
    @NonNull
    GrantType grantType;

    @UserEmail
    @Nullable
    String email;

    @UserPassword
    @Nullable
    String password;

    public boolean isEmailAndPasswordProvided() {
        return this.getEmail() != null && !this.getEmail().isBlank()
            && this.getPassword() != null && !this.getPassword().isBlank();
    }
}
