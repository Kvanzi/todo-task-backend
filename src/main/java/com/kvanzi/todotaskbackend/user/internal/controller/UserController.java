package com.kvanzi.todotaskbackend.user.internal.controller;

import com.kvanzi.todotaskbackend.shared.api.HttpApiResponse;
import com.kvanzi.todotaskbackend.user.api.dto.PrivateUserSummary;
import com.kvanzi.todotaskbackend.user.internal.dto.CreateUserRequest;
import com.kvanzi.todotaskbackend.user.internal.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

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
}
