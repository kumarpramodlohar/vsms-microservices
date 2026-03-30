package com.vsms.auth.domain.repository;

import com.vsms.auth.domain.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Menu repository for database operations.
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    /**
     * Find menu by menu name and active status.
     */
    Optional<Menu> findByMenuNameAndIsActiveTrue(String menuName);

    /**
     * Check if menu exists by menu name (case-insensitive) and active status.
     */
    boolean existsByMenuNameIgnoreCaseAndIsActiveTrue(String menuName);

    /**
     * Find all active menus.
     */
    List<Menu> findByIsActiveTrue();

    /**
     * Find menus by module ID and active status.
     */
    List<Menu> findByModuleIdAndIsActiveTrue(Long moduleId);

    /**
     * Find menus by active status.
     */
    @Query("SELECT m FROM Menu m WHERE m.isActive = :isActive")
    List<Menu> findByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Count active menus.
     */
    long countByIsActiveTrue();
}
