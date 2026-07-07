package com.kvanzi.todotaskbackend.todotask.internal.controller;

import com.kvanzi.todotaskbackend.shared.api.HttpApiResponse;
import com.kvanzi.todotaskbackend.todotask.internal.dto.CreateToDoTaskRequest;
import com.kvanzi.todotaskbackend.todotask.internal.dto.ToDoTaskSummary;
import com.kvanzi.todotaskbackend.todotask.internal.service.ToDoTaskService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo-tasks")
public class ToDoTaskController {
    private final ToDoTaskService toDoTaskService;

    @PostMapping
    public ResponseEntity<@NonNull HttpApiResponse<ToDoTaskSummary, Void>> createToDoTask(
        @NonNull @Valid @RequestBody CreateToDoTaskRequest request,
        @NonNull @AuthenticationPrincipal(expression = "id") UUID ownerId) {
        return HttpApiResponse.<ToDoTaskSummary>created()
            .data(toDoTaskService.createTask(ownerId, request))
            .build();
    }
}
