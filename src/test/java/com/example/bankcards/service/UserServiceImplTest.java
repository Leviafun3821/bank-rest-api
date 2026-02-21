package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldEncodePasswordAndSaveUser() {
        CreateUserRequest request = new CreateUserRequest("testuser", "pass123", "test@email.com", "ROLE_USER");
        User user = new User();
        User saved = new User();
        UserDTO dto = new UserDTO(UUID.randomUUID(), "testuser", "test@email.com", "ROLE_USER", null, null);

        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toDTO(saved)).thenReturn(dto);

        UserDTO result = userService.createUser(request);

        verify(passwordEncoder).encode("pass123");
        verify(userRepository).save(user);
        assertEquals("testuser", result.username());
        assertEquals("ROLE_USER", result.role());
    }

    @Test
    void getAllUsers_shouldReturnPagedUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();
        Page<User> page = new PageImpl<>(List.of(user));
        UserDTO dto = new UserDTO(UUID.randomUUID(), "testuser", "test@email.com", "ROLE_USER", null, null);

        when(userRepository.findAll(pageable)).thenReturn(page);
        when(userMapper.toDTO(user)).thenReturn(dto);

        Page<UserDTO> result = userService.getAllUsers(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).username());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void deleteUser_userNotFound_throwsException() {
        UUID id = UUID.randomUUID();

        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));
        verify(userRepository, never()).deleteById(id);
    }

    @Test
    void deleteUser_shouldCallRepositoryDelete() {
        UUID id = UUID.randomUUID();

        // Мокаем, что юзер существует, чтобы не бросалось исключение
        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(id);

        verify(userRepository).deleteById(id);
    }

}

