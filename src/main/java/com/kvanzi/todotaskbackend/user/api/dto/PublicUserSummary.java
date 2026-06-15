package com.kvanzi.todotaskbackend.user.api.dto;

import java.util.UUID;
import lombok.Value;

@Value
public class PublicUserSummary {
    UUID id;
    String firstName;
    String lastName;
}
