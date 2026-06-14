package com.kvanzi.todotaskbackend.user.internal.dto;

import lombok.Value;

@Value
public class CreateUserRequest {
    String firstName;
    String lastName;
    String email;
    String password;
}
