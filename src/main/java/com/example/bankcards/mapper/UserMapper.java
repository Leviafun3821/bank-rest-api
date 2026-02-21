package com.example.bankcards.mapper;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true) // шифруется в сервисе
    User toEntity(CreateUserRequest request);

    UserDTO toDTO(User user);

}
