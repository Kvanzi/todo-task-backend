package com.kvanzi.todotaskbackend.todotask.internal.mapper;

import com.kvanzi.todotaskbackend.todotask.internal.dto.CreateToDoTaskRequest;
import com.kvanzi.todotaskbackend.todotask.internal.dto.ToDoTaskSummary;
import com.kvanzi.todotaskbackend.todotask.internal.entity.ToDoTask;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ToDoTaskMapper {
    ToDoTask mapToEntity(CreateToDoTaskRequest request);

    ToDoTaskSummary mapToSummary(ToDoTask entity);
}
