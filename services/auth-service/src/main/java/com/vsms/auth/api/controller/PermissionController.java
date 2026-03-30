package com.vsms.auth.api.controller;

import com.vsms.auth.api.dto.CreatePermissionRequest;
import com.vsms.auth.api.dto.PermissionResponse;
import com.vsms.auth.api.dto.UpdatePermissionRequest;
import com.vsms.auth.application.service.PermissionService;
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
 * REST controller for permission management.
 */
@RestController
@RequestMapping("/api/v1/auth/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "Permission CRUD operations")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @Operation(summary = "Create permission", description = "Create a new permission")
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        PermissionResponse response = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Permission created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID", description = "Retrieve permission details by ID")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionById(@PathVariable Long id) {
        PermissionResponse response = permissionService.getPermissionById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/name/{permissionName}")
    @Operation(summary = "Get permission by name", description = "Retrieve permission details by permission name")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionByPermissionName(@PathVariable String permissionName) {
        PermissionResponse response = permissionService.getPermissionByPermissionName(permissionName);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get all permissions", description = "Retrieve all permissions")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {
        List<PermissionResponse> response = permissionService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active permissions", description = "Retrieve all active permissions")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllActivePermissions() {
        List<PermissionResponse> response = permissionService.getAllActivePermissions();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/resource/{resource}")
    @Operation(summary = "Get permissions by resource", description = "Retrieve permissions by resource")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getPermissionsByResource(@PathVariable String resource) {
        List<PermissionResponse> response = permissionService.getPermissionsByResource(resource);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/action/{action}")
    @Operation(summary = "Get permissions by action", description = "Retrieve permissions by action")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getPermissionsByAction(@PathVariable String action) {
        List<PermissionResponse> response = permissionService.getPermissionsByAction(action);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update permission", description = "Update permission details")
    public ResponseEntity<ApiResponse<PermissionResponse>> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePermissionRequest request) {
        PermissionResponse response = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Permission updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete permission", description = "Soft delete permission (set isActive to false)")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Permission deleted successfully"));
    }

    @GetMapping("/count")
    @Operation(summary = "Count active permissions", description = "Get count of active permissions")
    public ResponseEntity<ApiResponse<Long>> countActivePermissions() {
        long count = permissionService.countActivePermissions();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
