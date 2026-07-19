package com.kvanzi.todotaskbackend.todotask.internal.dto;

import com.kvanzi.todotaskbackend.shared.validation.ToDoTaskName;
import com.kvanzi.todotaskbackend.todotask.internal.entity.TaskPriority;
import com.kvanzi.todotaskbackend.todotask.internal.entity.TaskState;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.jspecify.annotations.NonNull;

@Value
@Builder
@Jacksonized
public class UpdateToDoTaskRequest {
    @ToDoTaskName
    @NotNull(message = "Name cannot be null.")
    @NonNull String name;

    @NotNull(message = "Priority cannot be null.")
    @NonNull TaskPriority priority;

    @NotNull(message = "State cannot be null.")
    @NonNull TaskState state;
}
