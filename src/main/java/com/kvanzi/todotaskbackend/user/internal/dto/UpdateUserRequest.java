package com.kvanzi.todotaskbackend.user.internal.dto;

import com.kvanzi.todotaskbackend.shared.validation.UserEmail;
import com.kvanzi.todotaskbackend.shared.validation.UserFirstName;
import com.kvanzi.todotaskbackend.shared.validation.UserLastName;
import com.kvanzi.todotaskbackend.user.internal.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.Value;

@Value
public class UpdateUserRequest implements UpdateUserStrategy {
    @UserFirstName
    @NotNull(message = "First name cannot be null.")
    @NonNull String firstName;

    @UserLastName
    @NotNull(message = "Last name cannot be null.")
    @NonNull String lastName;

    @UserEmail
    @NotNull(message = "Email cannot be null.")
    @NonNull String email;

    @Override
    public void applyTo(User user) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
    }
}
