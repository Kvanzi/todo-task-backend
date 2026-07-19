package com.kvanzi.todotaskbackend.todotask.internal.service;

import com.kvanzi.todotaskbackend.todotask.internal.repository.ToDoTaskRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ToDoTaskSecurityService {
    private final ToDoTaskRepository toDoTaskRepository;

    @Transactional(readOnly = true)
    public boolean isParticipant(@NonNull UUID taskId, @NonNull UUID userId) {
        return toDoTaskRepository.findById(taskId)
            .map(task -> task.isOwner(userId) || task.containsCollaborator(userId))
            .orElse(false);
    }
}
