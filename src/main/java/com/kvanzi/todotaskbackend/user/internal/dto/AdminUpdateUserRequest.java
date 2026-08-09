package com.kvanzi.todotaskbackend.user.internal.dto;

import com.kvanzi.todotaskbackend.shared.enumeration.Role;
import com.kvanzi.todotaskbackend.shared.validation.UserEmail;
import com.kvanzi.todotaskbackend.shared.validation.UserFirstName;
import com.kvanzi.todotaskbackend.shared.validation.UserLastName;
import com.kvanzi.todotaskbackend.user.internal.entity.User;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;
import lombok.Value;
import org.jspecify.annotations.NonNull;

@Value
public class AdminUpdateUserRequest implements UpdateUserStrategy {
    @UserFirstName
    @NotNull(message = "First name cannot be null.")
    @NonNull String firstName;

    @UserLastName
    @NotNull(message = "Last name cannot be null.")
    @NonNull String lastName;

    @UserEmail
    @NotNull(message = "Email cannot be null.")
    @NonNull String email;

    @NotNull(message = "Roles cannot be null.")
    @NonNull Set<Role> roles;

    @Override
    public void applyTo(User user) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.replaceRoles(roles);
    }

    @Override
    public @NonNull Optional<Set<Role>> getRolesToAssign() {
        return Optional.of(roles);
    }
}
