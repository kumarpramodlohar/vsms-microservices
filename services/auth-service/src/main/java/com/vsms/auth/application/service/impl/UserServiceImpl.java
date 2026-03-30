package com.vsms.auth.application.service.impl;

import com.vsms.auth.api.dto.CreateUserRequest;
import com.vsms.auth.api.dto.LoginRequest;
import com.vsms.auth.api.dto.LoginResponse;
import com.vsms.auth.api.dto.UpdateUserRequest;
import com.vsms.auth.api.dto.UserResponse;
import com.vsms.auth.application.service.UserService;
import com.vsms.auth.domain.entity.User;
import com.vsms.auth.domain.repository.UserRepository;
import com.vsms.common.exception.ResourceNotFoundException;
import com.vsms.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User service implementation.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating new user with userId: {}", request.getUserId());

        // Check if user already exists
        if (userRepository.existsByUserIdIgnoreCaseAndIsActiveTrue(request.getUserId())) {
            throw new ValidationException("User with ID '" + request.getUserId() + "' already exists");
        }

        // Check if email already exists
        if (request.getEmail() != null && userRepository.existsByEmailIgnoreCaseAndIsActiveTrue(request.getEmail())) {
            throw new ValidationException("User with email '" + request.getEmail() + "' already exists");
        }

        // Create new user
        User user = new User();
        user.setUserId(request.getUserId());
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setUserType(request.getUserType());
        user.setParentUser(request.getParentUser());
        user.setActive("Y");
        user.setIsActive(true);
        user.setCreatedBy(request.getCreatedBy());
        user.setUpdatedBy(request.getCreatedBy());

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        log.info("Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUserId(String userId) {
        log.info("Fetching user by userId: {}", userId);

        User user = userRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: " + userId));

        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllActiveUsers() {
        log.info("Fetching all active users");

        List<User> users = userRepository.findByIsActiveTrue();
        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");

        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Update fields if provided
        if (request.getUserName() != null) {
            user.setUserName(request.getUserName());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getEmail() != null) {
            // Check if email already exists for another user
            if (!user.getEmail().equalsIgnoreCase(request.getEmail()) &&
                    userRepository.existsByEmailIgnoreCaseAndIsActiveTrue(request.getEmail())) {
                throw new ValidationException("User with email '" + request.getEmail() + "' already exists");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getUserType() != null) {
            user.setUserType(request.getUserType());
        }
        if (request.getParentUser() != null) {
            user.setParentUser(request.getParentUser());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
            user.setIsActive("Y".equals(request.getActive()));
        }
        if (request.getUpdatedBy() != null) {
            user.setUpdatedBy(request.getUpdatedBy());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());

        return mapToUserResponse(updatedUser);
    }

    @Override
    public void deleteUser(UUID id) {
        log.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Soft delete
        user.setIsActive(false);
        user.setActive("N");
        userRepository.save(user);

        log.info("User soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("User login attempt for userId: {}", request.getUserId());

        User user = userRepository.findByUserIdAndIsActiveTrue(request.getUserId())
                .orElseThrow(() -> new ValidationException("Invalid user ID or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ValidationException("Invalid user ID or password");
        }

        // Generate JWT token (simplified - in production, use Spring Authorization Server)
        String token = generateJwtToken(user);

        log.info("User logged in successfully: {}", request.getUserId());

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600L) // 1 hour
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .userType(user.getUserType())
                .loginTime(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByUserType(String userType) {
        log.info("Fetching users by userType: {}", userType);

        List<User> users = userRepository.findByUserTypeAndIsActiveTrue(userType);
        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        log.info("Counting active users");
        return userRepository.countByIsActiveTrue();
    }

    /**
     * Map User entity to UserResponse DTO.
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .userType(user.getUserType())
                .parentUser(user.getParentUser())
                .active(user.getActive())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    /**
     * Generate JWT token for user.
     * Note: In production, use Spring Authorization Server for proper JWT generation.
     */
    private String generateJwtToken(User user) {
        // Simplified JWT generation - in production, use Spring Authorization Server
        // This is a placeholder implementation
        String header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String payload = String.format(
                "{\"sub\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"userType\":\"%s\",\"iat\":%d,\"exp\":%d}",
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getUserType(),
                System.currentTimeMillis() / 1000,
                System.currentTimeMillis() / 1000 + 3600
        );
        String signature = "dummy_signature_for_development";

        return header + "." + java.util.Base64.getEncoder().encodeToString(payload.getBytes()) + "." + signature;
    }
}
