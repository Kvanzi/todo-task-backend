package com.kvanzi.todotaskbackend.user.internal.dto;

import com.kvanzi.todotaskbackend.shared.enumeration.Role;
import com.kvanzi.todotaskbackend.user.internal.entity.User;
import java.util.Optional;
import java.util.Set;
import org.jspecify.annotations.NonNull;

public interface UpdateUserStrategy {
    void applyTo(User user);

    String getEmail();

    default @NonNull Optional<Set<Role>> getRolesToAssign() {
        return Optional.empty();
    }
}
