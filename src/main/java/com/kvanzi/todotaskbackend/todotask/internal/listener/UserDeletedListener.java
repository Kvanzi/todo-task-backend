package com.kvanzi.todotaskbackend.todotask.internal.listener;

import com.kvanzi.todotaskbackend.todotask.internal.repository.ToDoTaskRepository;
import com.kvanzi.todotaskbackend.user.api.event.UserDeletedEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletedListener {
    private final ToDoTaskRepository toDoTaskRepository;

    @ApplicationModuleListener
    public void onUserDeleted(UserDeletedEvent event) {
        UUID userId = event.userId();

        toDoTaskRepository.deleteAllByOwnerId(userId);
        int removedCollaborations = toDoTaskRepository.removeCollaboratorFromAllTasks(userId);

        log.info("Cleaned up tasks of deleted user '{}', removed {} collaborations", userId, removedCollaborations);
    }
}
