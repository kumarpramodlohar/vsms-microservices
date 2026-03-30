package com.vsms.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(min = 2, max = 255, message = "Username must be between 2 and 255 characters")
    private String userName;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 50, message = "User type must not exceed 50 characters")
    private String userType;

    @Size(max = 100, message = "Parent user must not exceed 100 characters")
    private String parentUser;

    private String active;

    private String updatedBy;
}
