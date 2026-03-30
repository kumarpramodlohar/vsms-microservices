package com.vsms.auth.application.service.impl;

import com.vsms.auth.api.dto.CreateUserRequest;
import com.vsms.auth.api.dto.LoginRequest;
import com.vsms.auth.api.dto.LoginResponse;
import com.vsms.auth.api.dto.UpdateUserRequest;
import com.vsms.auth.api.dto.UserResponse;
import com.vsms.auth.domain.entity.User;
import com.vsms.auth.domain.repository.UserRepository;
import com.vsms.common.exception.ResourceNotFoundException;
import com.vsms.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUserId("testuser");
        testUser.setUserName("Test User");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setUserType("ADMIN");
        testUser.setActive("Y");
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        testUser.setCreatedBy("system");
        testUser.setUpdatedBy("system");

        createUserRequest = CreateUserRequest.builder()
                .userId("newuser")
                .userName("New User")
                .password("password123")
                .email("new@example.com")
                .userType("MANAGER")
                .createdBy("system")
                .build();

        updateUserRequest = UpdateUserRequest.builder()
                .userName("Updated User")
                .email("updated@example.com")
                .updatedBy("system")
                .build();

        loginRequest = LoginRequest.builder()
                .userId("testuser")
                .password("password123")
                .build();
    }

    @Test
    void createUser_Success() {
        // Arrange
        when(userRepository.existsByUserIdIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponse response = userService.createUser(createUserRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getUserId(), response.getUserId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_DuplicateUserId_ThrowsException() {
        // Arrange
        when(userRepository.existsByUserIdIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> userService.createUser(createUserRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        // Arrange
        when(userRepository.existsByUserIdIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> userService.createUser(createUserRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));

        // Act
        UserResponse response = userService.getUserById(testUser.getId());

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getUserId(), response.getUserId());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(UUID.randomUUID()));
    }

    @Test
    void getUserByUserId_Success() {
        // Arrange
        when(userRepository.findByUserIdAndIsActiveTrue(anyString())).thenReturn(Optional.of(testUser));

        // Act
        UserResponse response = userService.getUserByUserId("testuser");

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getUserId(), response.getUserId());
    }

    @Test
    void getUserByUserId_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByUserIdAndIsActiveTrue(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUserId("nonexistent"));
    }

    @Test
    void getAllActiveUsers_Success() {
        // Arrange
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUserId("user2");
        user2.setIsActive(true);

        when(userRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testUser, user2));

        // Act
        List<UserResponse> response = userService.getAllActiveUsers();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUserId("user2");
        user2.setIsActive(false);

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        // Act
        List<UserResponse> response = userService.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void updateUser_Success() {
        // Arrange
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponse response = userService.updateUser(testUser.getId(), updateUserRequest);

        // Assert
        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(UUID.randomUUID(), updateUserRequest));
    }

    @Test
    void updateUser_DuplicateEmail_ThrowsException() {
        // Arrange
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(true);

        UpdateUserRequest requestWithEmail = UpdateUserRequest.builder()
                .email("duplicate@example.com")
                .build();

        // Act & Assert
        assertThrows(ValidationException.class, () -> userService.updateUser(testUser.getId(), requestWithEmail));
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.deleteUser(testUser.getId());

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        assertFalse(testUser.getIsActive());
        assertEquals("N", testUser.getActive());
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(UUID.randomUUID()));
    }

    @Test
    void login_Success() {
        // Arrange
        when(userRepository.findByUserIdAndIsActiveTrue(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        LoginResponse response = userService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(testUser.getUserId(), response.getUserId());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByUserIdAndIsActiveTrue(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, () -> userService.login(loginRequest));
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        // Arrange
        when(userRepository.findByUserIdAndIsActiveTrue(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(ValidationException.class, () -> userService.login(loginRequest));
    }

    @Test
    void getUsersByUserType_Success() {
        // Arrange
        when(userRepository.findByUserTypeAndIsActiveTrue(anyString())).thenReturn(Arrays.asList(testUser));

        // Act
        List<UserResponse> response = userService.getUsersByUserType("ADMIN");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("ADMIN", response.get(0).getUserType());
    }

    @Test
    void countActiveUsers_Success() {
        // Arrange
        when(userRepository.countByIsActiveTrue()).thenReturn(5L);

        // Act
        long count = userService.countActiveUsers();

        // Assert
        assertEquals(5L, count);
    }
}
