package com.vsms.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new menu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuRequest {

    @NotBlank(message = "Menu name is required")
    @Size(min = 2, max = 100, message = "Menu name must be between 2 and 100 characters")
    private String menuName;

    private Long moduleId;

    @Size(max = 255, message = "Menu URL must not exceed 255 characters")
    private String menuUrl;

    @Size(max = 50, message = "Menu icon must not exceed 50 characters")
    private String menuIcon;

    private Integer orderBy;

    private String createdBy;
}
