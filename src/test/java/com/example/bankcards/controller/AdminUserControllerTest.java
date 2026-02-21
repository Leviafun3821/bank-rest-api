package com.example.bankcards.controller;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UpdateUserRequest;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminUserController adminUserController;

    @Test
    void createUser_shouldCallServiceAndReturn201() {
        CreateUserRequest request = new CreateUserRequest("newuser", "pass123", "new@email.com", "ROLE_USER");
        UserDTO created = new UserDTO(UUID.randomUUID(), "newuser", "new@email.com", "ROLE_USER", null, null);

        when(userService.createUser(request)).thenReturn(created);

        ResponseEntity<UserDTO> response = adminUserController.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(created, response.getBody());
        verify(userService).createUser(request);
    }

    @Test
    void getAllUsers_shouldReturnPagedUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        UserDTO dto = new UserDTO(UUID.randomUUID(), "testuser", "test@email.com", "ROLE_USER", null, null);
        Page<UserDTO> page = new PageImpl<>(Collections.singletonList(dto));

        when(userService.getAllUsers(pageable)).thenReturn(page);

        ResponseEntity<Page<UserDTO>> response = adminUserController.getAllUsers(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        verify(userService).getAllUsers(pageable);
    }

    @Test
    void deleteUser_shouldCallServiceAndReturn204() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = adminUserController.deleteUser(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(id);
    }

    @Test
    void updateUser_shouldCallServiceAndReturn200() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("updated", "updated@email.com", "ROLE_ADMIN");
        UserDTO updated = new UserDTO(id, "updated", "updated@email.com", "ROLE_ADMIN", null, null);

        when(userService.updateUser(id, request)).thenReturn(updated);

        ResponseEntity<UserDTO> response = adminUserController.updateUser(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
        verify(userService).updateUser(id, request);
    }

}
