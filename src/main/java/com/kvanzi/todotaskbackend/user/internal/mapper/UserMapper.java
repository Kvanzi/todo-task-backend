package com.kvanzi.todotaskbackend.user.internal.mapper;

import com.kvanzi.todotaskbackend.shared.enumeration.Role;
import com.kvanzi.todotaskbackend.shared.security.IdentifiableUserDetailsImpl;
import com.kvanzi.todotaskbackend.user.api.dto.PrivateUserSummary;
import com.kvanzi.todotaskbackend.user.api.dto.PublicUserSummary;
import com.kvanzi.todotaskbackend.user.internal.dto.CreateUserRequest;
import com.kvanzi.todotaskbackend.user.internal.entity.User;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(target = "passwordHash", expression = "java(passwordHash)")
    User toEntity(CreateUserRequest requestDto, String passwordHash);

    PublicUserSummary toPublicUserSummary(User userEntity);

    PrivateUserSummary toPrivateUserSummary(User userEntity);

    @Mapping(source = "passwordHash", target = "password")
    @Mapping(source = "email", target = "username")
    @Mapping(source = "roles", target = "authorities")
    IdentifiableUserDetailsImpl toIdentifiableUserDetails(User userEntity);

    default @NonNull GrantedAuthority roleToAuthority(@NonNull Role role) {
        return new SimpleGrantedAuthority("ROLE_" + role.name());
    }
}
