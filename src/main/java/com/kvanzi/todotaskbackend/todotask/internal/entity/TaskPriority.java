package com.kvanzi.todotaskbackend.todotask.internal.entity;

import lombok.Getter;

public enum TaskPriority {
    LOW((short) 0),
    MEDIUM((short) 1),
    HIGH((short) 2);

    @Getter
    private final short weight;

    TaskPriority(short weight) {
        this.weight = weight;
    }
}
