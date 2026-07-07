package com.kvanzi.todotaskbackend.todotask.internal.repository;

import com.kvanzi.todotaskbackend.todotask.internal.entity.ToDoTask;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToDoTaskRepository extends JpaRepository<@NonNull ToDoTask, @NonNull UUID> {
}
