package com.vsms.auth.application.service;

import com.vsms.auth.api.dto.CreatePermissionRequest;
import com.vsms.auth.api.dto.PermissionResponse;
import com.vsms.auth.api.dto.UpdatePermissionRequest;

import java.util.List;

/**
 * Permission service interface.
 */
public interface PermissionService {

    /**
     * Create a new permission.
     */
    PermissionResponse createPermission(CreatePermissionRequest request);

    /**
     * Get permission by ID.
     */
    PermissionResponse getPermissionById(Long id);

    /**
     * Get permission by permission name.
     */
    PermissionResponse getPermissionByPermissionName(String permissionName);

    /**
     * Get all active permissions.
     */
    List<PermissionResponse> getAllActivePermissions();

    /**
     * Get all permissions.
     */
    List<PermissionResponse> getAllPermissions();

    /**
     * Get permissions by resource.
     */
    List<PermissionResponse> getPermissionsByResource(String resource);

    /**
     * Get permissions by action.
     */
    List<PermissionResponse> getPermissionsByAction(String action);

    /**
     * Update permission.
     */
    PermissionResponse updatePermission(Long id, UpdatePermissionRequest request);

    /**
     * Delete permission (soft delete).
     */
    void deletePermission(Long id);

    /**
     * Count active permissions.
     */
    long countActivePermissions();
}
