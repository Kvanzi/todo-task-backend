package com.kvanzi.todotaskbackend.todotask.internal.service;

import com.kvanzi.todotaskbackend.shared.utility.SortSanitizer;
import com.kvanzi.todotaskbackend.todotask.api.exception.OwnerCannotBeCollaboratorException;
import com.kvanzi.todotaskbackend.todotask.api.exception.ToDoTaskNotFoundException;
import com.kvanzi.todotaskbackend.todotask.internal.dto.*;
import com.kvanzi.todotaskbackend.todotask.internal.entity.TaskPriority;
import com.kvanzi.todotaskbackend.todotask.internal.entity.TaskState;
import com.kvanzi.todotaskbackend.todotask.internal.entity.ToDoTask;
import com.kvanzi.todotaskbackend.todotask.internal.mapper.ToDoTaskMapper;
import com.kvanzi.todotaskbackend.todotask.internal.repository.ToDoTaskRepository;
import com.kvanzi.todotaskbackend.todotask.internal.repository.ToDoTaskSpecifications;
import com.kvanzi.todotaskbackend.user.api.UserFacade;
import com.kvanzi.todotaskbackend.user.api.dto.PublicUserSummary;
import com.kvanzi.todotaskbackend.user.api.exception.UserNotFoundException;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToDoTaskService {
    private final ToDoTaskRepository toDoTaskRepository;
    private final ToDoTaskMapper toDoTaskMapper;
    private final UserFacade userFacade;

    private static final String OWNER_CANNOT_BE_COLLABORATOR_MESSAGE =
        "Task owner cannot be explicitly added as a collaborator";

    @Transactional
    public ToDoTaskSummary createTask(@NonNull UUID ownerId, @NonNull CreateToDoTaskRequest request) {
        if (!userFacade.existsByIdWithLock(ownerId)) {
            throw new UserNotFoundException("User not found.");
        }

        Set<UUID> collaboratorIds = request.getCollaboratorIds();

        if (collaboratorIds.contains(ownerId)) {
            throw new OwnerCannotBeCollaboratorException(OWNER_CANNOT_BE_COLLABORATOR_MESSAGE);
        }

        if (!collaboratorIds.isEmpty() && !userFacade.existsAllByIdsWithLock(collaboratorIds)) {
            throw new UserNotFoundException("One or more collaborators not found.");
        }

        ToDoTask task = toDoTaskMapper.mapToEntity(request);
        task.setOwnerId(ownerId);
        return toDoTaskMapper.mapToSummary(
            toDoTaskRepository.save(task)
        );
    }

    @Transactional(readOnly = true)
    public Page<@NonNull ToDoTaskSummary> findTasks(
        @NonNull UUID userId,
        @NonNull Role role,
        @Nullable TaskState state,
        @Nullable TaskPriority priority,
        @NonNull Pageable pageable
    ) {
        Sort safeSort = SortSanitizer.sanitize(
            pageable.getSort(),
            name -> ToDoTaskSortField.fromPublicName(name).getProperty()
        );
        Pageable safePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), safeSort);
        log.info("{}", safePageable);

        Specification<@NonNull ToDoTask> spec = Specification
            .where(ToDoTaskSpecifications.hasRole(userId, role))
            .and(ToDoTaskSpecifications.hasState(state))
            .and(ToDoTaskSpecifications.hasPriority(priority));

        return toDoTaskRepository.findAll(spec, safePageable)
            .map(toDoTaskMapper::mapToSummary);
    }

    @Transactional
    public ToDoTaskSummary updateTask(@NonNull UUID taskId, @NonNull UpdateToDoTaskRequest request) {
        ToDoTask task = toDoTaskRepository.findById(taskId)
            .orElseThrow(() -> new ToDoTaskNotFoundException("Task with id '%s' not found".formatted(taskId)));

        task.setName(request.getName());
        task.setPriority(request.getPriority());
        task.setState(request.getState());

        return toDoTaskMapper.mapToSummary(
            toDoTaskRepository.save(task)
        );
    }

    @Transactional
    public void deleteTask(@NonNull UUID taskId) {
        ToDoTask task = toDoTaskRepository.findById(taskId)
            .orElseThrow(() -> new ToDoTaskNotFoundException("Task with id '%s' not found.".formatted(taskId)));
        toDoTaskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public Page<@NonNull PublicUserSummary> findCollaborators(@NonNull UUID taskId, @NonNull Pageable pageable) {
        ToDoTask task = toDoTaskRepository.findById(taskId)
            .orElseThrow(() -> new ToDoTaskNotFoundException("Task with id '%s' not found.".formatted(taskId)));

        Sort safeSort = SortSanitizer.sanitize(
            pageable.getSort(),
            name -> CollaboratorSortField.fromPublicName(name).getProperty()
        );
        Pageable safePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), safeSort);

        return userFacade.getPublicUsersByIds(task.getCollaboratorIds(), safePageable);
    }

    @Transactional
    public void addCollaborator(@NonNull UUID taskId, @NonNull UUID collaboratorId) {
        ToDoTask task = toDoTaskRepository.findById(taskId)
            .orElseThrow(() -> new ToDoTaskNotFoundException("Task with id '%s' not found.".formatted(taskId)));

        if (task.isOwner(collaboratorId)) {
            throw new OwnerCannotBeCollaboratorException(OWNER_CANNOT_BE_COLLABORATOR_MESSAGE);
        }

        if (!userFacade.existsByIdWithLock(collaboratorId)) {
            throw new UserNotFoundException("User with id '%s' not found.".formatted(collaboratorId));
        }

        task.addCollaborator(collaboratorId);
        toDoTaskRepository.save(task);
    }

    @Transactional
    public void removeCollaborator(@NonNull UUID taskId, @NonNull UUID collaboratorId) {
        ToDoTask task = toDoTaskRepository.findById(taskId)
            .orElseThrow(() -> new ToDoTaskNotFoundException("Task with id '%s' not found.".formatted(taskId)));

        task.removeCollaborator(collaboratorId);
        toDoTaskRepository.save(task);
    }
}
