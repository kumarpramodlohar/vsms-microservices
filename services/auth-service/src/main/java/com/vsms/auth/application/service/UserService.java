package com.vsms.auth.application.service;

import com.vsms.auth.api.dto.CreateUserRequest;
import com.vsms.auth.api.dto.LoginRequest;
import com.vsms.auth.api.dto.LoginResponse;
import com.vsms.auth.api.dto.UpdateUserRequest;
import com.vsms.auth.api.dto.UserResponse;

import java.util.List;
import java.util.UUID;

/**
 * User and authentication service interface.
 */
public interface UserService {

    /**
     * Create a new user.
     */
    UserResponse createUser(CreateUserRequest request);

    /**
     * Get user by ID.
     */
    UserResponse getUserById(UUID id);

    /**
     * Get user by userId.
     */
    UserResponse getUserByUserId(String userId);

    /**
     * Get all active users.
     */
    List<UserResponse> getAllActiveUsers();

    /**
     * Get all users.
     */
    List<UserResponse> getAllUsers();

    /**
     * Update user.
     */
    UserResponse updateUser(UUID id, UpdateUserRequest request);

    /**
     * Delete user (soft delete).
     */
    void deleteUser(UUID id);

    /**
     * Login and return JWT token.
     */
    LoginResponse login(LoginRequest request);

    /**
     * Get users by userType.
     */
    List<UserResponse> getUsersByUserType(String userType);

    /**
     * Count active users.
     */
    long countActiveUsers();
}
