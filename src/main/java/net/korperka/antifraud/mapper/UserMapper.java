package net.korperka.antifraud.mapper;

import net.korperka.antifraud.dto.request.RegisterRequest;
import net.korperka.antifraud.dto.request.UserCreateRequest;
import net.korperka.antifraud.dto.response.UserResponse;
import net.korperka.antifraud.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserCreateRequest dto);
}
