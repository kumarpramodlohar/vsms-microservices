package com.vsms.auth.domain.repository;

import com.vsms.auth.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Role repository for database operations.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by role name and active status.
     */
    Optional<Role> findByRoleNameAndIsActiveTrue(String roleName);

    /**
     * Check if role exists by role name (case-insensitive) and active status.
     */
    boolean existsByRoleNameIgnoreCaseAndIsActiveTrue(String roleName);

    /**
     * Find all active roles.
     */
    List<Role> findByIsActiveTrue();

    /**
     * Find roles by active status.
     */
    @Query("SELECT r FROM Role r WHERE r.isActive = :isActive")
    List<Role> findByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Count active roles.
     */
    long countByIsActiveTrue();
}
