package com.kvanzi.todotaskbackend.todotask.internal.dto;

import com.kvanzi.todotaskbackend.todotask.internal.entity.TaskPriority;
import com.kvanzi.todotaskbackend.todotask.internal.entity.TaskState;
import java.util.UUID;
import lombok.Value;

@Value
public class ToDoTaskSummary {
    UUID id;
    String name;
    TaskPriority priority;
    TaskState state;
    UUID ownerId;
}
