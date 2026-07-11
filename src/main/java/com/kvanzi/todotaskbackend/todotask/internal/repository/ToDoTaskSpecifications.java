package com.kvanzi.todotaskbackend.todotask.internal.repository;

import com.kvanzi.todotaskbackend.todotask.internal.dto.Role;
import com.kvanzi.todotaskbackend.todotask.internal.entity.TaskPriority;
import com.kvanzi.todotaskbackend.todotask.internal.entity.TaskState;
import com.kvanzi.todotaskbackend.todotask.internal.entity.ToDoTask;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToDoTaskSpecifications {
    public static Specification<@NonNull ToDoTask> isOwner(@NonNull UUID userId) {
        return (root, query, cb) -> cb.equal(root.get("ownerId"), userId);
    }

    public static Specification<@NonNull ToDoTask> isCollaborator(@NonNull UUID userId) {
        return (root, query, cb) -> cb.isMember(userId, root.get("collaboratorIds"));
    }

    public static Specification<@NonNull ToDoTask> hasRole(@NonNull UUID userId, @NonNull Role role) {
        return switch (role) {
            case OWNER -> isOwner(userId);
            case COLLABORATOR -> isCollaborator(userId);
            case ALL -> Specification.where(isOwner(userId)).or(isCollaborator(userId));
        };
    }

    public static Specification<@NonNull ToDoTask> hasState(@Nullable TaskState state) {
        return (root, query, cb) -> state == null ? null : cb.equal(root.get("state"), state);
    }

    public static Specification<@NonNull ToDoTask> hasPriority(@Nullable TaskPriority priority) {
        return (root, query, cb) -> priority == null ? null : cb.equal(root.get("priority"), priority);
    }
}
