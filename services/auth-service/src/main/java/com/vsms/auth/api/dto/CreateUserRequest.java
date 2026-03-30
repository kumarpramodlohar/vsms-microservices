package com.vsms.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "User ID is required")
    @Size(min = 3, max = 100, message = "User ID must be between 3 and 100 characters")
    private String userId;

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 255, message = "Username must be between 2 and 255 characters")
    private String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 50, message = "User type must not exceed 50 characters")
    private String userType;

    @Size(max = 100, message = "Parent user must not exceed 100 characters")
    private String parentUser;

    private String createdBy;
}
