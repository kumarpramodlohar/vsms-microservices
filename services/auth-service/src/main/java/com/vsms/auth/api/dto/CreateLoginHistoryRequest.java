package com.vsms.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for creating a new login history entry.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLoginHistoryRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    private String macAddr;

    @NotNull(message = "Login date time is required")
    private LocalDateTime loginDtTime;

    private LocalDateTime logoutDtTime;

    private String createdBy;
}
