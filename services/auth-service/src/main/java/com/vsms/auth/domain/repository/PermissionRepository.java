package com.vsms.auth.domain.repository;

import com.vsms.auth.domain.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Permission repository for database operations.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Find permission by permission name and active status.
     */
    Optional<Permission> findByPermissionNameAndIsActiveTrue(String permissionName);

    /**
     * Check if permission exists by permission name (case-insensitive) and active status.
     */
    boolean existsByPermissionNameIgnoreCaseAndIsActiveTrue(String permissionName);

    /**
     * Find all active permissions.
     */
    List<Permission> findByIsActiveTrue();

    /**
     * Find permissions by resource and active status.
     */
    List<Permission> findByResourceAndIsActiveTrue(String resource);

    /**
     * Find permissions by action and active status.
     */
    List<Permission> findByActionAndIsActiveTrue(String action);

    /**
     * Find permissions by active status.
     */
    @Query("SELECT p FROM Permission p WHERE p.isActive = :isActive")
    List<Permission> findByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Count active permissions.
     */
    long countByIsActiveTrue();
}
