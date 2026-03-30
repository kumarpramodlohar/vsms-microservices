package com.vsms.auth.api.controller;

import com.vsms.auth.api.dto.CreateUserRequest;
import com.vsms.auth.api.dto.LoginRequest;
import com.vsms.auth.api.dto.LoginResponse;
import com.vsms.auth.api.dto.UpdateUserRequest;
import com.vsms.auth.api.dto.UserResponse;
import com.vsms.auth.application.service.UserService;
import com.vsms.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for user management and authentication.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth & User Management", description = "Identity and access management — user CRUD, login, JWT issuance")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/users")
    @Operation(summary = "Create user", description = "Create a new user account")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User created successfully"));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by UUID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/users/userid/{userId}")
    @Operation(summary = "Get user by userId", description = "Retrieve user details by userId")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUserId(@PathVariable String userId) {
        UserResponse response = userService.getUserByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieve all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/users/active")
    @Operation(summary = "Get all active users", description = "Retrieve all active users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllActiveUsers() {
        List<UserResponse> response = userService.getAllActiveUsers();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/users/type/{userType}")
    @Operation(summary = "Get users by type", description = "Retrieve users by user type")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByUserType(@PathVariable String userType) {
        List<UserResponse> response = userService.getUsersByUserType(userType);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user", description = "Update user details")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "User updated successfully"));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user", description = "Soft delete user (set isActive to false)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }

    @GetMapping("/users/count")
    @Operation(summary = "Count active users", description = "Get count of active users")
    public ResponseEntity<ApiResponse<Long>> countActiveUsers() {
        long count = userService.countActiveUsers();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/jwks")
    @Operation(summary = "Get JWKS", description = "Get JSON Web Key Set for JWT verification")
    public ResponseEntity<ApiResponse<String>> jwks() {
        // In production, Spring Authorization Server provides this automatically
        return ResponseEntity.ok(ApiResponse.success("JWKS endpoint - configure Spring Authorization Server"));
    }
}
