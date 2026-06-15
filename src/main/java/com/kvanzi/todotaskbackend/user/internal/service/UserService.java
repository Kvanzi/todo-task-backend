package com.kvanzi.todotaskbackend.user.internal.service;

import com.kvanzi.todotaskbackend.user.api.dto.PrivateUserSummary;
import com.kvanzi.todotaskbackend.user.api.enumeration.Role;
import com.kvanzi.todotaskbackend.user.api.exception.EmailTakenException;
import com.kvanzi.todotaskbackend.user.internal.dto.CreateUserRequest;
import com.kvanzi.todotaskbackend.user.internal.entity.User;
import com.kvanzi.todotaskbackend.user.internal.mapper.UserMapper;
import com.kvanzi.todotaskbackend.user.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
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
    public PrivateUserSummary createUser(@NonNull CreateUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new EmailTakenException(EMAIL_TAKEN_MESSAGE);
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());
        User user = userMapper.toEntity(request, passwordHash);
        user.addRole(Role.USER);

        try {
            return userMapper.toPrivateUserSummary(userRepository.saveAndFlush(user));
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
