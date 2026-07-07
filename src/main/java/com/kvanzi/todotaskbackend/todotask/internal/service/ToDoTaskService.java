package com.kvanzi.todotaskbackend.todotask.internal.service;

import com.kvanzi.todotaskbackend.todotask.internal.dto.CreateToDoTaskRequest;
import com.kvanzi.todotaskbackend.todotask.internal.dto.ToDoTaskSummary;
import com.kvanzi.todotaskbackend.todotask.internal.entity.ToDoTask;
import com.kvanzi.todotaskbackend.todotask.internal.mapper.ToDoTaskMapper;
import com.kvanzi.todotaskbackend.todotask.internal.repository.ToDoTaskRepository;
import com.kvanzi.todotaskbackend.user.api.UserFacade;
import com.kvanzi.todotaskbackend.user.api.exception.UserNotFoundException;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ToDoTaskService {
    private final ToDoTaskRepository toDoTaskRepository;
    private final ToDoTaskMapper toDoTaskMapper;
    private final UserFacade userFacade;

    @Transactional
    public ToDoTaskSummary createTask(@NonNull UUID ownerId, @NonNull CreateToDoTaskRequest request) {
        if (!userFacade.existsById(ownerId)) {
            throw new UserNotFoundException("User not found.");
        }

        Set<UUID> collaboratorIds = request.getCollaboratorIds();
        if (!collaboratorIds.isEmpty() && !userFacade.existsAllByIds(collaboratorIds)) {
            throw new UserNotFoundException("One or more collaborators not found.");
        }

        ToDoTask task = toDoTaskMapper.mapToEntity(request);
        task.setOwnerId(ownerId);
        return toDoTaskMapper.mapToSummary(
            toDoTaskRepository.save(task)
        );
    }
}
