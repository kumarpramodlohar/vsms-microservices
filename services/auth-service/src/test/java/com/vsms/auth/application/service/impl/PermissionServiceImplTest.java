package com.vsms.auth.application.service.impl;

import com.vsms.auth.api.dto.CreatePermissionRequest;
import com.vsms.auth.api.dto.PermissionResponse;
import com.vsms.auth.api.dto.UpdatePermissionRequest;
import com.vsms.auth.domain.entity.Permission;
import com.vsms.auth.domain.repository.PermissionRepository;
import com.vsms.common.exception.ResourceNotFoundException;
import com.vsms.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PermissionServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission testPermission;
    private CreatePermissionRequest createPermissionRequest;
    private UpdatePermissionRequest updatePermissionRequest;

    @BeforeEach
    void setUp() {
        testPermission = new Permission();
        testPermission.setId(1L);
        testPermission.setPermissionName("READ_USER");
        testPermission.setDescription("Read user permission");
        testPermission.setResource("USER");
        testPermission.setAction("READ");
        testPermission.setActive("Y");
        testPermission.setIsActive(true);
        testPermission.setCreatedAt(LocalDateTime.now());
        testPermission.setUpdatedAt(LocalDateTime.now());
        testPermission.setCreatedBy("system");
        testPermission.setUpdatedBy("system");

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
    void createPermission_Success() {
        // Arrange
        when(permissionRepository.existsByPermissionNameIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(testPermission);

        // Act
        PermissionResponse response = permissionService.createPermission(createPermissionRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testPermission.getPermissionName(), response.getPermissionName());
        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    void createPermission_DuplicatePermissionName_ThrowsException() {
        // Arrange
        when(permissionRepository.existsByPermissionNameIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> permissionService.createPermission(createPermissionRequest));
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void getPermissionById_Success() {
        // Arrange
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.of(testPermission));

        // Act
        PermissionResponse response = permissionService.getPermissionById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(testPermission.getId(), response.getId());
        assertEquals(testPermission.getPermissionName(), response.getPermissionName());
    }

    @Test
    void getPermissionById_NotFound_ThrowsException() {
        // Arrange
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> permissionService.getPermissionById(1L));
    }

    @Test
    void getPermissionByPermissionName_Success() {
        // Arrange
        when(permissionRepository.findByPermissionNameAndIsActiveTrue(anyString())).thenReturn(Optional.of(testPermission));

        // Act
        PermissionResponse response = permissionService.getPermissionByPermissionName("READ_USER");

        // Assert
        assertNotNull(response);
        assertEquals(testPermission.getPermissionName(), response.getPermissionName());
    }

    @Test
    void getPermissionByPermissionName_NotFound_ThrowsException() {
        // Arrange
        when(permissionRepository.findByPermissionNameAndIsActiveTrue(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> permissionService.getPermissionByPermissionName("READ_USER"));
    }

    @Test
    void getAllActivePermissions_Success() {
        // Arrange
        Permission permission2 = new Permission();
        permission2.setId(2L);
        permission2.setPermissionName("WRITE_USER");
        permission2.setIsActive(true);

        when(permissionRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testPermission, permission2));

        // Act
        List<PermissionResponse> response = permissionService.getAllActivePermissions();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void getAllPermissions_Success() {
        // Arrange
        Permission permission2 = new Permission();
        permission2.setId(2L);
        permission2.setPermissionName("WRITE_USER");
        permission2.setIsActive(false);

        when(permissionRepository.findAll()).thenReturn(Arrays.asList(testPermission, permission2));

        // Act
        List<PermissionResponse> response = permissionService.getAllPermissions();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void getPermissionsByResource_Success() {
        // Arrange
        when(permissionRepository.findByResourceAndIsActiveTrue(anyString())).thenReturn(Arrays.asList(testPermission));

        // Act
        List<PermissionResponse> response = permissionService.getPermissionsByResource("USER");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getPermissionsByAction_Success() {
        // Arrange
        when(permissionRepository.findByActionAndIsActiveTrue(anyString())).thenReturn(Arrays.asList(testPermission));

        // Act
        List<PermissionResponse> response = permissionService.getPermissionsByAction("READ");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void updatePermission_Success() {
        // Arrange
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.of(testPermission));
        when(permissionRepository.save(any(Permission.class))).thenReturn(testPermission);

        // Act
        PermissionResponse response = permissionService.updatePermission(1L, updatePermissionRequest);

        // Assert
        assertNotNull(response);
        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    void updatePermission_NotFound_ThrowsException() {
        // Arrange
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> permissionService.updatePermission(1L, updatePermissionRequest));
    }

    @Test
    void updatePermission_DuplicatePermissionName_ThrowsException() {
        // Arrange
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.of(testPermission));
        when(permissionRepository.existsByPermissionNameIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(true);

        UpdatePermissionRequest requestWithDuplicateName = UpdatePermissionRequest.builder()
                .permissionName("WRITE_USER")
                .build();

        // Act & Assert
        assertThrows(ValidationException.class, () -> permissionService.updatePermission(1L, requestWithDuplicateName));
    }

    @Test
    void deletePermission_Success() {
        // Arrange
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.of(testPermission));
        when(permissionRepository.save(any(Permission.class))).thenReturn(testPermission);

        // Act
        permissionService.deletePermission(1L);

        // Assert
        verify(permissionRepository, times(1)).save(any(Permission.class));
        assertFalse(testPermission.getIsActive());
        assertEquals("N", testPermission.getActive());
    }

    @Test
    void deletePermission_NotFound_ThrowsException() {
        // Arrange
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> permissionService.deletePermission(1L));
    }

    @Test
    void countActivePermissions_Success() {
        // Arrange
        when(permissionRepository.countByIsActiveTrue()).thenReturn(10L);

        // Act
        long count = permissionService.countActivePermissions();

        // Assert
        assertEquals(10L, count);
    }
}
