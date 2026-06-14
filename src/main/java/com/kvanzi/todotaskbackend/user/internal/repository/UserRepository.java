package com.kvanzi.todotaskbackend.user.internal.repository;

import com.kvanzi.todotaskbackend.user.internal.entity.User;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<@NonNull User, @NonNull UUID> {
    boolean existsByEmailIgnoreCase(@NonNull String email);
}
