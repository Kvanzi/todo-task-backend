package com.kvanzi.todotaskbackend.user.internal.mapper;

import com.kvanzi.todotaskbackend.user.api.dto.PrivateUserSummary;
import com.kvanzi.todotaskbackend.user.api.dto.PublicUserSummary;
import com.kvanzi.todotaskbackend.user.internal.dto.CreateUserRequest;
import com.kvanzi.todotaskbackend.user.internal.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(target = "passwordHash", expression = "java(passwordHash)")
    User toEntity(CreateUserRequest requestDto, String passwordHash);

    PublicUserSummary toPublicUserSummary(User userEntity);

    PrivateUserSummary toPrivateUserSummary(User userEntity);
}
