package com.kvanzi.todotaskbackend.user.api;

import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetails;
import com.kvanzi.todotaskbackend.user.internal.mapper.UserMapper;
import com.kvanzi.todotaskbackend.user.internal.service.UserService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;
    private final UserMapper userMapper;

    public Optional<IdentifiableUserDetails> findSecurityUserDetailsByEmailIgnoreCase(@NonNull String email) {
        return userService.findUserByEmailIgnoreCase(email)
            .map(userMapper::toIdentifiableUserDetails);
    }

    public Optional<IdentifiableUserDetails> findSecurityUserDetailsById(@NonNull UUID id) {
        return userService.findUserById(id)
            .map(userMapper::toIdentifiableUserDetails);
    }
}
