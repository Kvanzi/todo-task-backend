package com.kvanzi.todotaskbackend.user.internal.service;

import com.kvanzi.todotaskbackend.shared.enumeration.Role;
import com.kvanzi.todotaskbackend.user.api.dto.PrivateUserSummary;
import com.kvanzi.todotaskbackend.user.api.dto.PublicUserSummary;
import com.kvanzi.todotaskbackend.user.api.exception.EmailTakenException;
import com.kvanzi.todotaskbackend.user.api.exception.UserNotFoundException;
import com.kvanzi.todotaskbackend.user.internal.dto.CreateUserRequest;
import com.kvanzi.todotaskbackend.user.internal.dto.UpdateUserRequest;
import com.kvanzi.todotaskbackend.user.internal.entity.User;
import com.kvanzi.todotaskbackend.user.internal.mapper.UserMapper;
import com.kvanzi.todotaskbackend.user.internal.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private static final String EMAIL_TAKEN_MESSAGE = "This email is taken by another user.";

    /**
     * Creates a new user.
     *
     * @throws EmailTakenException if a user with the provided email already exists
     */
    @Transactional
    public @NonNull PrivateUserSummary createUser(@NonNull CreateUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new EmailTakenException(EMAIL_TAKEN_MESSAGE);
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());
        User user = userMapper.toEntity(request, passwordHash);
        user.addRole(Role.USER);

        return saveAndFlushSafely(() -> userMapper.toPrivateUserSummary(userRepository.saveAndFlush(user)));
    }

    public @NonNull Optional<User> findUserByEmailIgnoreCase(@NonNull String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public @NonNull Optional<User> findUserById(@NonNull UUID id) {
        return userRepository.findById(id);
    }

    public @NonNull Page<@NonNull PrivateUserSummary> getPrivateUsers(@NonNull Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(userMapper::toPrivateUserSummary);
    }

    public @NonNull Page<@NonNull PublicUserSummary> getPublicUsers(@NonNull Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(userMapper::toPublicUserSummary);
    }

    @Transactional
    public @NonNull PrivateUserSummary updateUser(@NonNull UUID userId, @NonNull UpdateUserRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User with id '%s' not found".formatted(userId)));

        validateEmailUniquenessOrThrow(user.getEmail(), request.getEmail());
        request.applyTo(user);

        return saveAndFlushSafely(() -> userMapper.toPrivateUserSummary(userRepository.saveAndFlush(user)));
    }

    /**
     * @throws EmailTakenException when email is taken by another user.
     */
    private void validateEmailUniquenessOrThrow(@NonNull String oldEmail, @NonNull String newEmail) {
        if (oldEmail.equalsIgnoreCase(newEmail)) {
            return;
        }

        if (!userRepository.existsByEmailIgnoreCase(newEmail)) {
            return;
        }

        throw new EmailTakenException(EMAIL_TAKEN_MESSAGE);
    }

    private <T> T saveAndFlushSafely(Supplier<T> saveAction) {
        try {
            return saveAction.get();
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException cve) {
                String constraintName = cve.getConstraintName();
                if (constraintName != null && constraintName.contains("email")) {
                    throw new EmailTakenException(EMAIL_TAKEN_MESSAGE);
                }
            }
            throw e;
        }
    }
}
