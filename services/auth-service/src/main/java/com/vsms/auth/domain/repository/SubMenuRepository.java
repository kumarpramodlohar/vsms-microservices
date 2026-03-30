package com.vsms.auth.domain.repository;

import com.vsms.auth.domain.entity.SubMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SubMenu repository for database operations.
 */
@Repository
public interface SubMenuRepository extends JpaRepository<SubMenu, Long> {

    /**
     * Find sub menu by sub menu name and active status.
     */
    Optional<SubMenu> findBySubMenuNameAndIsActiveTrue(String subMenuName);

    /**
     * Check if sub menu exists by sub menu name (case-insensitive) and active status.
     */
    boolean existsBySubMenuNameIgnoreCaseAndIsActiveTrue(String subMenuName);

    /**
     * Find all active sub menus.
     */
    List<SubMenu> findByIsActiveTrue();

    /**
     * Find sub menus by menu ID and active status.
     */
    List<SubMenu> findByMenuIdAndIsActiveTrue(Long menuId);

    /**
     * Find sub menus by active status.
     */
    @Query("SELECT sm FROM SubMenu sm WHERE sm.isActive = :isActive")
    List<SubMenu> findByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Count active sub menus.
     */
    long countByIsActiveTrue();
}
