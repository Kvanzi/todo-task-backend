package com.kvanzi.todotaskbackend.todotask.internal.dto;

import com.kvanzi.todotaskbackend.todotask.api.exception.InvalidSortPropertyException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ToDoTaskSortField {
    CREATED_AT("createdAt"),
    PRIORITY("priorityWeight");

    @Getter
    private final String property;

    public static ToDoTaskSortField fromPublicName(String name) {
        return Arrays.stream(values())
            .filter(f -> f.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new InvalidSortPropertyException(name));
    }
}
