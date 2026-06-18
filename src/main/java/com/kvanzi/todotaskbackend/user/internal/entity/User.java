package com.kvanzi.todotaskbackend.user.internal.entity;

import com.kvanzi.todotaskbackend.shared.persistence.BaseEntity;
import com.kvanzi.todotaskbackend.shared.enumeration.Role;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "users"
)
@SuppressWarnings("java:S2160")
public class User extends BaseEntity {
    @Column(name = "first_name", nullable = false)
    @NonNull
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NonNull
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    @NonNull
    private String email;

    @Column(name = "password_hash", nullable = false)
    @NonNull
    private String passwordHash;

    @Column(name = "last_password_changed_at")
    @Nullable
    private Instant lastPasswordChangedAt;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(
            name = "user_id",
            nullable = false,
            referencedColumnName = "id"
        )
    )
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @NonNull
    private Set<Role> roles = new HashSet<>();

    public @NonNull Set<Role> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    public @NonNull User addRole(@NonNull Role role) {
        Objects.requireNonNull(role, "Role cannot be null");
        this.roles.add(role);
        return this;
    }

    public @NonNull User removeRole(@NonNull Role role) {
        Objects.requireNonNull(role, "Role cannot be null");
        this.roles.remove(role);
        return this;
    }

    public boolean hasRole(@NonNull Role role) {
        Objects.requireNonNull(role, "Role cannot be null");
        return this.roles.contains(role);
    }
}
