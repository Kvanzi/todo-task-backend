package com.kvanzi.todotaskbackend.todotask.internal.repository;

import com.kvanzi.todotaskbackend.todotask.internal.entity.ToDoTask;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ToDoTaskRepository
    extends JpaRepository<@NonNull ToDoTask, @NonNull UUID>, JpaSpecificationExecutor<@NonNull ToDoTask> {
    void deleteAllByOwnerId(@NonNull UUID ownerId);

    @Modifying
    @Query(value = "delete from todo_tasks_collaborators where collaborator_id = :userId", nativeQuery = true)
    int removeCollaboratorFromAllTasks(@Param("userId") UUID userId);
}
