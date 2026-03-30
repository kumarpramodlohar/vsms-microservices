package com.vsms.auth.application.service;

import com.vsms.auth.api.dto.CreateRoleRequest;
import com.vsms.auth.api.dto.RoleResponse;
import com.vsms.auth.api.dto.UpdateRoleRequest;

import java.util.List;

/**
 * Role service interface.
 */
public interface RoleService {

    /**
     * Create a new role.
     */
    RoleResponse createRole(CreateRoleRequest request);

    /**
     * Get role by ID.
     */
    RoleResponse getRoleById(Long id);

    /**
     * Get role by role name.
     */
    RoleResponse getRoleByRoleName(String roleName);

    /**
     * Get all active roles.
     */
    List<RoleResponse> getAllActiveRoles();

    /**
     * Get all roles.
     */
    List<RoleResponse> getAllRoles();

    /**
     * Update role.
     */
    RoleResponse updateRole(Long id, UpdateRoleRequest request);

    /**
     * Delete role (soft delete).
     */
    void deleteRole(Long id);

    /**
     * Count active roles.
     */
    long countActiveRoles();
}
