package com.kvanzi.todotaskbackend.todotask.internal.dto;

import com.kvanzi.todotaskbackend.todotask.api.exception.InvalidSortPropertyException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CollaboratorSortField {
    FIRST_NAME("firstName"),
    LAST_NAME("lastName");

    @Getter
    private final String property;

    public static CollaboratorSortField fromPublicName(String name) {
        return Arrays.stream(values())
            .filter(f -> f.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new InvalidSortPropertyException(name));
    }
}