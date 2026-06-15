package com.kvanzi.todotaskbackend.user.api.dto;

import com.kvanzi.todotaskbackend.user.api.enumeration.Role;
import java.util.Set;
import java.util.UUID;
import lombok.Value;

@Value
public class PrivateUserSummary {
    UUID id;
    String firstName;
    String lastName;
    String email;
    Set<Role> roles;
}
