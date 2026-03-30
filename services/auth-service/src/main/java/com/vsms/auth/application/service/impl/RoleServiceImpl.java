package com.vsms.auth.application.service.impl;

import com.vsms.auth.api.dto.CreateRoleRequest;
import com.vsms.auth.api.dto.RoleResponse;
import com.vsms.auth.api.dto.UpdateRoleRequest;
import com.vsms.auth.application.service.RoleService;
import com.vsms.auth.domain.entity.Role;
import com.vsms.auth.domain.repository.RoleRepository;
import com.vsms.common.exception.ResourceNotFoundException;
import com.vsms.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Role service implementation.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleResponse createRole(CreateRoleRequest request) {
        log.info("Creating new role with roleName: {}", request.getRoleName());

        // Check if role already exists
        if (roleRepository.existsByRoleNameIgnoreCaseAndIsActiveTrue(request.getRoleName())) {
            throw new ValidationException("Role with name '" + request.getRoleName() + "' already exists");
        }

        // Create new role
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role.setActive("Y");
        role.setIsActive(true);
        role.setCreatedBy(request.getCreatedBy());
        role.setUpdatedBy(request.getCreatedBy());

        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully with ID: {}", savedRole.getId());

        return mapToRoleResponse(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        log.info("Fetching role by ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        return mapToRoleResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleByRoleName(String roleName) {
        log.info("Fetching role by roleName: {}", roleName);

        Role role = roleRepository.findByRoleNameAndIsActiveTrue(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with roleName: " + roleName));

        return mapToRoleResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllActiveRoles() {
        log.info("Fetching all active roles");

        List<Role> roles = roleRepository.findByIsActiveTrue();
        return roles.stream()
                .map(this::mapToRoleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.info("Fetching all roles");

        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(this::mapToRoleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse updateRole(Long id, UpdateRoleRequest request) {
        log.info("Updating role with ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        // Update fields if provided
        if (request.getRoleName() != null) {
            // Check if role name already exists for another role
            if (!role.getRoleName().equalsIgnoreCase(request.getRoleName()) &&
                    roleRepository.existsByRoleNameIgnoreCaseAndIsActiveTrue(request.getRoleName())) {
                throw new ValidationException("Role with name '" + request.getRoleName() + "' already exists");
            }
            role.setRoleName(request.getRoleName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            role.setActive(request.getActive());
            role.setIsActive("Y".equals(request.getActive()));
        }
        if (request.getUpdatedBy() != null) {
            role.setUpdatedBy(request.getUpdatedBy());
        }

        Role updatedRole = roleRepository.save(role);
        log.info("Role updated successfully with ID: {}", updatedRole.getId());

        return mapToRoleResponse(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Deleting role with ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        // Soft delete
        role.setIsActive(false);
        role.setActive("N");
        roleRepository.save(role);

        log.info("Role soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveRoles() {
        log.info("Counting active roles");
        return roleRepository.countByIsActiveTrue();
    }

    /**
     * Map Role entity to RoleResponse DTO.
     */
    private RoleResponse mapToRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .active(role.getActive())
                .isActive(role.getIsActive())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .createdBy(role.getCreatedBy())
                .updatedBy(role.getUpdatedBy())
                .build();
    }
}
