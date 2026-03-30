package com.vsms.auth.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsms.auth.api.dto.CreateUserRequest;
import com.vsms.auth.api.dto.LoginRequest;
import com.vsms.auth.api.dto.LoginResponse;
import com.vsms.auth.api.dto.UpdateUserRequest;
import com.vsms.auth.api.dto.UserResponse;
import com.vsms.auth.application.service.UserService;
import com.vsms.common.exception.ResourceNotFoundException;
import com.vsms.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UserResponse testUserResponse;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        testUserResponse = UserResponse.builder()
                .id(UUID.randomUUID())
                .userId("testuser")
                .userName("Test User")
                .email("test@example.com")
                .userType("ADMIN")
                .active("Y")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build();

        createUserRequest = CreateUserRequest.builder()
                .userId("newuser")
                .userName("New User")
                .password("password123")
                .email("new@example.com")
                .userType("USER")
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

        loginResponse = LoginResponse.builder()
                .accessToken("test-access-token")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .userId("testuser")
                .userName("Test User")
                .email("test@example.com")
                .userType("ADMIN")
                .loginTime(LocalDateTime.now())
                .build();
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(testUserResponse);

        mockMvc.perform(post("/api/v1/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.userId", is("testuser")));

        verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }

    @Test
    void createUser_DuplicateUserId_ThrowsException() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new ValidationException("User ID already exists"));

        mockMvc.perform(post("/api/v1/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));

        verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }

    @Test
    void getUserById_Success() throws Exception {
        UUID userId = testUserResponse.getId();
        when(userService.getUserById(eq(userId))).thenReturn(testUserResponse);

        mockMvc.perform(get("/api/v1/auth/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.userId", is("testuser")));

        verify(userService, times(1)).getUserById(eq(userId));
    }

    @Test
    void getUserById_NotFound_ThrowsException() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.getUserById(eq(userId)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/auth/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(userService, times(1)).getUserById(eq(userId));
    }

    @Test
    void getUserByUserId_Success() throws Exception {
        when(userService.getUserByUserId(eq("testuser"))).thenReturn(testUserResponse);

        mockMvc.perform(get("/api/v1/auth/users/userid/{userId}", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.userId", is("testuser")));

        verify(userService, times(1)).getUserByUserId(eq("testuser"));
    }

    @Test
    void getAllUsers_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(testUserResponse);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].userId", is("testuser")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getAllActiveUsers_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(testUserResponse);
        when(userService.getAllActiveUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/auth/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].userId", is("testuser")));

        verify(userService, times(1)).getAllActiveUsers();
    }

    @Test
    void getUsersByUserType_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(testUserResponse);
        when(userService.getUsersByUserType(eq("ADMIN"))).thenReturn(users);

        mockMvc.perform(get("/api/v1/auth/users/type/{userType}", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].userType", is("ADMIN")));

        verify(userService, times(1)).getUsersByUserType(eq("ADMIN"));
    }

    @Test
    void updateUser_Success() throws Exception {
        UUID userId = testUserResponse.getId();
        when(userService.updateUser(eq(userId), any(UpdateUserRequest.class))).thenReturn(testUserResponse);

        mockMvc.perform(put("/api/v1/auth/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.userId", is("testuser")));

        verify(userService, times(1)).updateUser(eq(userId), any(UpdateUserRequest.class));
    }

    @Test
    void updateUser_NotFound_ThrowsException() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.updateUser(eq(userId), any(UpdateUserRequest.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(put("/api/v1/auth/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(userService, times(1)).updateUser(eq(userId), any(UpdateUserRequest.class));
    }

    @Test
    void deleteUser_Success() throws Exception {
        UUID userId = testUserResponse.getId();
        doNothing().when(userService).deleteUser(eq(userId));

        mockMvc.perform(delete("/api/v1/auth/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(userService, times(1)).deleteUser(eq(userId));
    }

    @Test
    void deleteUser_NotFound_ThrowsException() throws Exception {
        UUID userId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).deleteUser(eq(userId));

        mockMvc.perform(delete("/api/v1/auth/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(userService, times(1)).deleteUser(eq(userId));
    }

    @Test
    void login_Success() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accessToken", is("test-access-token")))
                .andExpect(jsonPath("$.data.userId", is("testuser")));

        verify(userService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_InvalidCredentials_ThrowsException() throws Exception {
        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new ValidationException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));

        verify(userService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void countActiveUsers_Success() throws Exception {
        when(userService.countActiveUsers()).thenReturn(10L);

        mockMvc.perform(get("/api/v1/auth/users/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is(10)));

        verify(userService, times(1)).countActiveUsers();
    }
}
