package com.kvanzi.todotaskbackend.user.internal.dto;

import com.kvanzi.todotaskbackend.shared.validation.UserFirstName;
import com.kvanzi.todotaskbackend.shared.validation.UserLastName;
import com.kvanzi.todotaskbackend.shared.validation.UserPassword;
import jakarta.validation.constraints.Email;
import lombok.Value;

@Value
public class CreateUserRequest {
    @UserFirstName
    String firstName;

    @UserLastName
    String lastName;

    @Email
    String email;

    @UserPassword
    String password;
}
