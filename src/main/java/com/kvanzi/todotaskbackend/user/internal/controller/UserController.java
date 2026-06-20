package com.kvanzi.todotaskbackend.user.internal.controller;

import com.kvanzi.todotaskbackend.shared.api.HttpApiResponse;
import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetails;
import com.kvanzi.todotaskbackend.user.api.dto.PrivateUserSummary;
import com.kvanzi.todotaskbackend.user.api.exception.UserNotFoundException;
import com.kvanzi.todotaskbackend.user.internal.dto.CreateUserRequest;
import com.kvanzi.todotaskbackend.user.internal.mapper.UserMapper;
import com.kvanzi.todotaskbackend.user.internal.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<@NonNull HttpApiResponse<@NonNull PrivateUserSummary, Void>> createUser(
        @Valid @RequestBody CreateUserRequest request) {
        PrivateUserSummary createdUser = userService.createUser(request);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdUser.getId())
            .toUri();

        return HttpApiResponse.<PrivateUserSummary>created()
            .data(createdUser)
            .message("Successful registration")
            .location(location)
            .build();
    }


    @GetMapping("/me")
    public ResponseEntity<@NonNull HttpApiResponse<@NonNull PrivateUserSummary, Void>> getCurrentUser(
        @NonNull @AuthenticationPrincipal IdentifiableUserDetails userDetails) {
        PrivateUserSummary userSummary = userService.findUserById(userDetails.getId())
            .map(userMapper::toPrivateUserSummary)
            .orElseThrow(() -> new UserNotFoundException("Authenticated user not found."));

        return HttpApiResponse.<PrivateUserSummary>ok()
            .data(userSummary)
            .build();
    }
}
