package com.kvanzi.todotaskbackend.todotask.internal.controller;

import com.kvanzi.todotaskbackend.shared.api.HttpApiResponse;
import com.kvanzi.todotaskbackend.shared.dto.PageResponse;
import com.kvanzi.todotaskbackend.todotask.internal.dto.CreateToDoTaskRequest;
import com.kvanzi.todotaskbackend.todotask.internal.dto.Role;
import com.kvanzi.todotaskbackend.todotask.internal.dto.ToDoTaskSummary;
import com.kvanzi.todotaskbackend.todotask.internal.entity.TaskPriority;
import com.kvanzi.todotaskbackend.todotask.internal.entity.TaskState;
import com.kvanzi.todotaskbackend.todotask.internal.service.ToDoTaskService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<@NonNull PageResponse<ToDoTaskSummary>> getMyTasks(
        @NonNull @AuthenticationPrincipal(expression = "id") UUID userId,
        @RequestParam(defaultValue = "ALL") Role role,
        @RequestParam(required = false) TaskState state,
        @RequestParam(required = false) TaskPriority priority,
        Pageable pageable
    ) {
        Page<@NonNull ToDoTaskSummary> page = toDoTaskService.findTasks(userId, role, state, priority, pageable);
        return ResponseEntity.ok(PageResponse.from(page));
    }
}
