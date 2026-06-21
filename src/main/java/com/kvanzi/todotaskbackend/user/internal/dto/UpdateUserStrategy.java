package com.kvanzi.todotaskbackend.user.internal.dto;

import com.kvanzi.todotaskbackend.user.internal.entity.User;

public interface UpdateUserStrategy {
    void applyTo(User user);

    String getEmail();
}
