package com.kvanzi.todotaskbackend.user.api.event;

import java.util.UUID;

public record UserDeletedEvent(UUID userId) {
}
