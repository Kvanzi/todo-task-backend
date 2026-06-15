package com.kvanzi.todotaskbackend.user.internal.dto;

import com.kvanzi.todotaskbackend.shared.validation.UserEmail;
import com.kvanzi.todotaskbackend.shared.validation.UserFirstName;
import com.kvanzi.todotaskbackend.shared.validation.UserLastName;
import com.kvanzi.todotaskbackend.shared.validation.UserPassword;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.jspecify.annotations.NonNull;

@Value
public class CreateUserRequest {
    @UserFirstName
    @NonNull
    @NotNull(message = "First name cannot be null.")
    String firstName;

    @UserLastName
    @NonNull
    @NotNull(message = "Last name cannot be null.")
    String lastName;

    @UserEmail
    @NonNull
    @NotNull(message = "Email cannot be null.")
    String email;

    @UserPassword
    @NonNull
    @NotNull(message = "Password cannot be null.")
    String password;
}
