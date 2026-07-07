package com.kvanzi.todotaskbackend.todotask.internal.dto;

import com.kvanzi.todotaskbackend.shared.validation.ToDoTaskName;
import com.kvanzi.todotaskbackend.todotask.internal.entity.TaskPriority;
import com.kvanzi.todotaskbackend.todotask.internal.entity.TaskState;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.jspecify.annotations.NonNull;

@Value
@Builder
@Jacksonized
public class CreateToDoTaskRequest {
    @ToDoTaskName
    @NotNull(message = "Name cannot be null.")
    @NonNull
    String name;

    @NonNull
    TaskPriority priority;

    @NonNull
    TaskState state;

    @NonNull
    @Builder.Default
    Set<UUID> collaboratorIds = new HashSet<>();
}
