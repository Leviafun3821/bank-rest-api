package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UpdateUserRequest;
import com.example.bankcards.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserDTO createUser(CreateUserRequest request);
    Page<UserDTO> getAllUsers(Pageable pageable);
    UserDTO getUserById(UUID id);
    void deleteUser(UUID id);
    UserDTO updateUser(UUID id, UpdateUserRequest request);

}
