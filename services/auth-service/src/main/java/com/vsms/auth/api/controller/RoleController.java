package com.vsms.auth.api.controller;

import com.vsms.auth.api.dto.CreateRoleRequest;
import com.vsms.auth.api.dto.RoleResponse;
import com.vsms.auth.api.dto.UpdateRoleRequest;
import com.vsms.auth.application.service.RoleService;
import com.vsms.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for role management.
 */
@RestController
@RequestMapping("/api/v1/auth/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "Role CRUD operations")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create role", description = "Create a new role")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Role created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Retrieve role details by ID")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        RoleResponse response = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/name/{roleName}")
    @Operation(summary = "Get role by name", description = "Retrieve role details by role name")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleByRoleName(@PathVariable String roleName) {
        RoleResponse response = roleService.getRoleByRoleName(roleName);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieve all roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> response = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active roles", description = "Retrieve all active roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllActiveRoles() {
        List<RoleResponse> response = roleService.getAllActiveRoles();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role", description = "Update role details")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        RoleResponse response = roleService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Role updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role", description = "Soft delete role (set isActive to false)")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Role deleted successfully"));
    }

    @GetMapping("/count")
    @Operation(summary = "Count active roles", description = "Get count of active roles")
    public ResponseEntity<ApiResponse<Long>> countActiveRoles() {
        long count = roleService.countActiveRoles();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
