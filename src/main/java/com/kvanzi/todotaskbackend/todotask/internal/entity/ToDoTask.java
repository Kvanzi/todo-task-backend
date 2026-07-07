package com.kvanzi.todotaskbackend.todotask.internal.entity;

import com.kvanzi.todotaskbackend.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "todo_tasks",
    indexes = {
        @Index(name = "idx_todo_task_owner_id_sort_created_at", columnList = "owner_id,created_at DESC"),
        @Index(name = "idx_todo_task_owner_id_sort_priority_weight", columnList = "owner_id,priority_weight DESC"),
    }
)
@SuppressWarnings("java:S2160")
public class ToDoTask extends BaseEntity {
    @Version
    @Builder.Default
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority;

    @Setter(AccessLevel.NONE)
    @Column(name = "priority_weight", nullable = false)
    private short priorityWeight;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private TaskState state;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Getter(AccessLevel.NONE)
    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "todo_tasks_collaborators",
        joinColumns = @JoinColumn(
            name = "task_id",
            nullable = false,
            referencedColumnName = "id"
        )
    )
    @Column(name = "collaborator_id", nullable = false)
    private Set<UUID> collaboratorIds = new HashSet<>();

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
        this.priorityWeight = priority.getWeight();
    }
}
