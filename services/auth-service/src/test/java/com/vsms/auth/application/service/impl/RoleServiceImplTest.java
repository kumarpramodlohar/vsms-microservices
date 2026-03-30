package com.vsms.auth.application.service.impl;

import com.vsms.auth.api.dto.CreateRoleRequest;
import com.vsms.auth.api.dto.RoleResponse;
import com.vsms.auth.api.dto.UpdateRoleRequest;
import com.vsms.auth.domain.entity.Role;
import com.vsms.auth.domain.repository.RoleRepository;
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
 * Unit tests for RoleServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role testRole;
    private CreateRoleRequest createRoleRequest;
    private UpdateRoleRequest updateRoleRequest;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setRoleName("ADMIN");
        testRole.setDescription("Administrator role");
        testRole.setActive("Y");
        testRole.setIsActive(true);
        testRole.setCreatedAt(LocalDateTime.now());
        testRole.setUpdatedAt(LocalDateTime.now());
        testRole.setCreatedBy("system");
        testRole.setUpdatedBy("system");

        createRoleRequest = CreateRoleRequest.builder()
                .roleName("MANAGER")
                .description("Manager role")
                .createdBy("system")
                .build();

        updateRoleRequest = UpdateRoleRequest.builder()
                .roleName("MANAGER")
                .description("Updated manager role")
                .updatedBy("system")
                .build();
    }

    @Test
    void createRole_Success() {
        // Arrange
        when(roleRepository.existsByRoleNameIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // Act
        RoleResponse response = roleService.createRole(createRoleRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testRole.getRoleName(), response.getRoleName());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void createRole_DuplicateRoleName_ThrowsException() {
        // Arrange
        when(roleRepository.existsByRoleNameIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> roleService.createRole(createRoleRequest));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void getRoleById_Success() {
        // Arrange
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(testRole));

        // Act
        RoleResponse response = roleService.getRoleById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(testRole.getId(), response.getId());
        assertEquals(testRole.getRoleName(), response.getRoleName());
    }

    @Test
    void getRoleById_NotFound_ThrowsException() {
        // Arrange
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleById(1L));
    }

    @Test
    void getRoleByRoleName_Success() {
        // Arrange
        when(roleRepository.findByRoleNameAndIsActiveTrue(anyString())).thenReturn(Optional.of(testRole));

        // Act
        RoleResponse response = roleService.getRoleByRoleName("ADMIN");

        // Assert
        assertNotNull(response);
        assertEquals(testRole.getRoleName(), response.getRoleName());
    }

    @Test
    void getRoleByRoleName_NotFound_ThrowsException() {
        // Arrange
        when(roleRepository.findByRoleNameAndIsActiveTrue(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleByRoleName("ADMIN"));
    }

    @Test
    void getAllActiveRoles_Success() {
        // Arrange
        Role role2 = new Role();
        role2.setId(2L);
        role2.setRoleName("USER");
        role2.setIsActive(true);

        when(roleRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testRole, role2));

        // Act
        List<RoleResponse> response = roleService.getAllActiveRoles();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void getAllRoles_Success() {
        // Arrange
        Role role2 = new Role();
        role2.setId(2L);
        role2.setRoleName("USER");
        role2.setIsActive(false);

        when(roleRepository.findAll()).thenReturn(Arrays.asList(testRole, role2));

        // Act
        List<RoleResponse> response = roleService.getAllRoles();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void updateRole_Success() {
        // Arrange
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(testRole));
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // Act
        RoleResponse response = roleService.updateRole(1L, updateRoleRequest);

        // Assert
        assertNotNull(response);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void updateRole_NotFound_ThrowsException() {
        // Arrange
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> roleService.updateRole(1L, updateRoleRequest));
    }

    @Test
    void updateRole_DuplicateRoleName_ThrowsException() {
        // Arrange
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(testRole));
        when(roleRepository.existsByRoleNameIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(true);

        UpdateRoleRequest requestWithDuplicateName = UpdateRoleRequest.builder()
                .roleName("USER")
                .build();

        // Act & Assert
        assertThrows(ValidationException.class, () -> roleService.updateRole(1L, requestWithDuplicateName));
    }

    @Test
    void deleteRole_Success() {
        // Arrange
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(testRole));
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // Act
        roleService.deleteRole(1L);

        // Assert
        verify(roleRepository, times(1)).save(any(Role.class));
        assertFalse(testRole.getIsActive());
        assertEquals("N", testRole.getActive());
    }

    @Test
    void deleteRole_NotFound_ThrowsException() {
        // Arrange
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> roleService.deleteRole(1L));
    }

    @Test
    void countActiveRoles_Success() {
        // Arrange
        when(roleRepository.countByIsActiveTrue()).thenReturn(5L);

        // Act
        long count = roleService.countActiveRoles();

        // Assert
        assertEquals(5L, count);
    }
}
