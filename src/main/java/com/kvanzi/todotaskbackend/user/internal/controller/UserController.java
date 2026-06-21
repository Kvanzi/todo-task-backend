package com.kvanzi.todotaskbackend.user.internal.controller;

import com.kvanzi.todotaskbackend.shared.api.HttpApiResponse;
import com.kvanzi.todotaskbackend.shared.dto.PageResponse;
import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetails;
import com.kvanzi.todotaskbackend.user.api.dto.PrivateUserSummary;
import com.kvanzi.todotaskbackend.user.api.exception.UserNotFoundException;
import com.kvanzi.todotaskbackend.user.internal.dto.CreateUserRequest;
import com.kvanzi.todotaskbackend.user.internal.dto.UpdateUserRequest;
import com.kvanzi.todotaskbackend.user.internal.mapper.UserMapper;
import com.kvanzi.todotaskbackend.user.internal.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


    @GetMapping
    public ResponseEntity<@NonNull HttpApiResponse<PageResponse<?>, Void>> getUsers(
        @RequestParam(name = "pageNumber", defaultValue = "0")
        @Min(value = 0, message = "The page number parameter must be no less than {value}.")
        @Max(value = 500, message = "The page number parameter must be no more than {value}.")
        int pageNumber,

        @RequestParam(name = "pageSize", defaultValue = "15")
        @Min(value = 1, message = "The page size parameter must be no less than {value}.")
        @Max(value = 50, message = "The page size parameter must be no more than {value}.")
        int pageSize,

        @Nullable @AuthenticationPrincipal IdentifiableUserDetails userDetails
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        PageResponse<?> response = isAdmin(userDetails)
            ? PageResponse.from(userService.getPrivateUsers(pageable))
            : PageResponse.from(userService.getPublicUsers(pageable));

        return HttpApiResponse.<PageResponse<?>>ok()
            .data(response)
            .build();
    }

    @PutMapping("/me")
    public ResponseEntity<@NonNull HttpApiResponse<PrivateUserSummary, Void>> updateMe(
        @NonNull @AuthenticationPrincipal(expression = "id") UUID userId,
        @Valid @RequestBody UpdateUserRequest updateRequest) {
        PrivateUserSummary updatedUser = userService.updateUser(userId, updateRequest);
        return HttpApiResponse.<PrivateUserSummary>ok()
            .data(updatedUser)
            .build();
    }

    private boolean isAdmin(@Nullable IdentifiableUserDetails userDetails) {
        if (userDetails == null) {
            return false;
        }
        return userDetails.getAuthorities().stream()
            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
