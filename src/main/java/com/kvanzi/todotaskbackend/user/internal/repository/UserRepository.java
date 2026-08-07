package com.kvanzi.todotaskbackend.user.internal.repository;

import com.kvanzi.todotaskbackend.shared.enumeration.Role;
import com.kvanzi.todotaskbackend.user.internal.entity.User;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<@NonNull User, @NonNull UUID> {
    boolean existsByEmailIgnoreCase(@NonNull String email);

    @NonNull Optional<User> findByEmailIgnoreCase(@NonNull String email);

    long countByIdIn(@NonNull Set<@NonNull UUID> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u join u.roles r where r = :role order by u.id")
    List<User> lockUsersWithRole(@Param("role") Role role);
}
