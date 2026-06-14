package com.kvanzi.todotaskbackend.user.internal.service;

import com.kvanzi.todotaskbackend.user.api.dto.PrivateUserSummary;
import com.kvanzi.todotaskbackend.user.api.enumeration.Role;
import com.kvanzi.todotaskbackend.user.api.exception.EmailTakenException;
import com.kvanzi.todotaskbackend.user.internal.dto.CreateUserRequest;
import com.kvanzi.todotaskbackend.user.internal.entity.User;
import com.kvanzi.todotaskbackend.user.internal.mapper.UserMapper;
import com.kvanzi.todotaskbackend.user.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public PrivateUserSummary createUser(@NonNull CreateUserRequest request) throws EmailTakenException {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new EmailTakenException("This email is taken by another user.");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());
        User user = userMapper.toEntity(request, passwordHash);
        user.addRole(Role.USER);

        return userMapper.toPrivateUserSummary(userRepository.save(user));
    }
}
