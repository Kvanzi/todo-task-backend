package com.kvanzi.todotaskbackend.user.internal.entity;

import com.kvanzi.todotaskbackend.shared.persistence.BaseEntity;
import com.kvanzi.todotaskbackend.user.api.enumeration.Role;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

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
    private Set<Role> roles = new HashSet<>();
}
