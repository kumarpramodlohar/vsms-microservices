package com.vsms.auth.application.service.impl;

import com.vsms.auth.api.dto.CreatePermissionRequest;
import com.vsms.auth.api.dto.PermissionResponse;
import com.vsms.auth.api.dto.UpdatePermissionRequest;
import com.vsms.auth.application.service.PermissionService;
import com.vsms.auth.domain.entity.Permission;
import com.vsms.auth.domain.repository.PermissionRepository;
import com.vsms.common.exception.ResourceNotFoundException;
import com.vsms.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Permission service implementation.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        log.info("Creating new permission with permissionName: {}", request.getPermissionName());

        // Check if permission already exists
        if (permissionRepository.existsByPermissionNameIgnoreCaseAndIsActiveTrue(request.getPermissionName())) {
            throw new ValidationException("Permission with name '" + request.getPermissionName() + "' already exists");
        }

        // Create new permission
        Permission permission = new Permission();
        permission.setPermissionName(request.getPermissionName());
        permission.setDescription(request.getDescription());
        permission.setResource(request.getResource());
        permission.setAction(request.getAction());
        permission.setActive("Y");
        permission.setIsActive(true);
        permission.setCreatedBy(request.getCreatedBy());
        permission.setUpdatedBy(request.getCreatedBy());

        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created successfully with ID: {}", savedPermission.getId());

        return mapToPermissionResponse(savedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getPermissionById(Long id) {
        log.info("Fetching permission by ID: {}", id);

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with ID: " + id));

        return mapToPermissionResponse(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getPermissionByPermissionName(String permissionName) {
        log.info("Fetching permission by permissionName: {}", permissionName);

        Permission permission = permissionRepository.findByPermissionNameAndIsActiveTrue(permissionName)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with permissionName: " + permissionName));

        return mapToPermissionResponse(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllActivePermissions() {
        log.info("Fetching all active permissions");

        List<Permission> permissions = permissionRepository.findByIsActiveTrue();
        return permissions.stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        log.info("Fetching all permissions");

        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByResource(String resource) {
        log.info("Fetching permissions by resource: {}", resource);

        List<Permission> permissions = permissionRepository.findByResourceAndIsActiveTrue(resource);
        return permissions.stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByAction(String action) {
        log.info("Fetching permissions by action: {}", action);

        List<Permission> permissions = permissionRepository.findByActionAndIsActiveTrue(action);
        return permissions.stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionResponse updatePermission(Long id, UpdatePermissionRequest request) {
        log.info("Updating permission with ID: {}", id);

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with ID: " + id));

        // Update fields if provided
        if (request.getPermissionName() != null) {
            // Check if permission name already exists for another permission
            if (!permission.getPermissionName().equalsIgnoreCase(request.getPermissionName()) &&
                    permissionRepository.existsByPermissionNameIgnoreCaseAndIsActiveTrue(request.getPermissionName())) {
                throw new ValidationException("Permission with name '" + request.getPermissionName() + "' already exists");
            }
            permission.setPermissionName(request.getPermissionName());
        }
        if (request.getDescription() != null) {
            permission.setDescription(request.getDescription());
        }
        if (request.getResource() != null) {
            permission.setResource(request.getResource());
        }
        if (request.getAction() != null) {
            permission.setAction(request.getAction());
        }
        if (request.getActive() != null) {
            permission.setActive(request.getActive());
            permission.setIsActive("Y".equals(request.getActive()));
        }
        if (request.getUpdatedBy() != null) {
            permission.setUpdatedBy(request.getUpdatedBy());
        }

        Permission updatedPermission = permissionRepository.save(permission);
        log.info("Permission updated successfully with ID: {}", updatedPermission.getId());

        return mapToPermissionResponse(updatedPermission);
    }

    @Override
    public void deletePermission(Long id) {
        log.info("Deleting permission with ID: {}", id);

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with ID: " + id));

        // Soft delete
        permission.setIsActive(false);
        permission.setActive("N");
        permissionRepository.save(permission);

        log.info("Permission soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActivePermissions() {
        log.info("Counting active permissions");
        return permissionRepository.countByIsActiveTrue();
    }

    /**
     * Map Permission entity to PermissionResponse DTO.
     */
    private PermissionResponse mapToPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .permissionName(permission.getPermissionName())
                .description(permission.getDescription())
                .resource(permission.getResource())
                .action(permission.getAction())
                .active(permission.getActive())
                .isActive(permission.getIsActive())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .createdBy(permission.getCreatedBy())
                .updatedBy(permission.getUpdatedBy())
                .build();
    }
}
