package com.vsms.auth.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsms.auth.api.dto.CreatePermissionRequest;
import com.vsms.auth.api.dto.PermissionResponse;
import com.vsms.auth.api.dto.UpdatePermissionRequest;
import com.vsms.auth.application.service.PermissionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PermissionController.
 */
class PermissionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private PermissionController permissionController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private PermissionResponse testPermissionResponse;
    private CreatePermissionRequest createPermissionRequest;
    private UpdatePermissionRequest updatePermissionRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(permissionController).build();

        testPermissionResponse = PermissionResponse.builder()
                .id(1L)
                .permissionName("READ_USER")
                .description("Read user permission")
                .resource("USER")
                .action("READ")
                .active("Y")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build();

        createPermissionRequest = CreatePermissionRequest.builder()
                .permissionName("WRITE_USER")
                .description("Write user permission")
                .resource("USER")
                .action("WRITE")
                .createdBy("system")
                .build();

        updatePermissionRequest = UpdatePermissionRequest.builder()
                .permissionName("WRITE_USER")
                .description("Updated write user permission")
                .resource("USER")
                .action("WRITE")
                .updatedBy("system")
                .build();
    }

    @Test
    void createPermission_Success() throws Exception {
        // Arrange
        when(permissionService.createPermission(any(CreatePermissionRequest.class))).thenReturn(testPermissionResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPermissionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.permissionName").value(testPermissionResponse.getPermissionName()));
    }

    @Test
    void createPermission_DuplicatePermissionName_ThrowsException() throws Exception {
        // Arrange
        when(permissionService.createPermission(any(CreatePermissionRequest.class)))
                .thenThrow(new ValidationException("Permission with name 'WRITE_USER' already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPermissionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getPermissionById_Success() throws Exception {
        // Arrange
        when(permissionService.getPermissionById(anyLong())).thenReturn(testPermissionResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/permissions/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.permissionName").value(testPermissionResponse.getPermissionName()));
    }

    @Test
    void getPermissionById_NotFound_ThrowsException() throws Exception {
        // Arrange
        when(permissionService.getPermissionById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Permission not found with ID: 1"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/permissions/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getPermissionByPermissionName_Success() throws Exception {
        // Arrange
        when(permissionService.getPermissionByPermissionName(anyString())).thenReturn(testPermissionResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/permissions/name/{permissionName}", "READ_USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.permissionName").value(testPermissionResponse.getPermissionName()));
    }

    @Test
    void getAllPermissions_Success() throws Exception {
        // Arrange
        PermissionResponse permission2 = PermissionResponse.builder()
                .id(2L)
                .permissionName("WRITE_USER")
                .description("Write user permission")
                .resource("USER")
                .action("WRITE")
                .active("Y")
                .isActive(true)
                .build();

        List<PermissionResponse> permissions = Arrays.asList(testPermissionResponse, permission2);
        when(permissionService.getAllPermissions()).thenReturn(permissions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getAllActivePermissions_Success() throws Exception {
        // Arrange
        List<PermissionResponse> permissions = Arrays.asList(testPermissionResponse);
        when(permissionService.getAllActivePermissions()).thenReturn(permissions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/permissions/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void getPermissionsByResource_Success() throws Exception {
        // Arrange
        List<PermissionResponse> permissions = Arrays.asList(testPermissionResponse);
        when(permissionService.getPermissionsByResource(anyString())).thenReturn(permissions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/permissions/resource/{resource}", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void getPermissionsByAction_Success() throws Exception {
        // Arrange
        List<PermissionResponse> permissions = Arrays.asList(testPermissionResponse);
        when(permissionService.getPermissionsByAction(anyString())).thenReturn(permissions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/permissions/action/{action}", "READ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void updatePermission_Success() throws Exception {
        // Arrange
        when(permissionService.updatePermission(anyLong(), any(UpdatePermissionRequest.class))).thenReturn(testPermissionResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/auth/permissions/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePermissionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.permissionName").value(testPermissionResponse.getPermissionName()));
    }

    @Test
    void updatePermission_NotFound_ThrowsException() throws Exception {
        // Arrange
        when(permissionService.updatePermission(anyLong(), any(UpdatePermissionRequest.class)))
                .thenThrow(new ResourceNotFoundException("Permission not found with ID: 1"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/auth/permissions/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePermissionRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deletePermission_Success() throws Exception {
        // Arrange
        doNothing().when(permissionService).deletePermission(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/auth/permissions/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deletePermission_NotFound_ThrowsException() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Permission not found with ID: 1"))
                .when(permissionService).deletePermission(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/auth/permissions/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void countActivePermissions_Success() throws Exception {
        // Arrange
        when(permissionService.countActivePermissions()).thenReturn(10L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/permissions/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(10));
    }
}
