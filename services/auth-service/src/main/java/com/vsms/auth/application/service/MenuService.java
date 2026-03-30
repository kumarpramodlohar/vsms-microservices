package com.vsms.auth.application.service;

import com.vsms.auth.api.dto.CreateMenuRequest;
import com.vsms.auth.api.dto.MenuResponse;
import com.vsms.auth.api.dto.UpdateMenuRequest;

import java.util.List;

/**
 * Menu service interface.
 */
public interface MenuService {

    /**
     * Create a new menu.
     */
    MenuResponse createMenu(CreateMenuRequest request);

    /**
     * Get menu by ID.
     */
    MenuResponse getMenuById(Long id);

    /**
     * Get menu by menu name.
     */
    MenuResponse getMenuByMenuName(String menuName);

    /**
     * Get all active menus.
     */
    List<MenuResponse> getAllActiveMenus();

    /**
     * Get all menus.
     */
    List<MenuResponse> getAllMenus();

    /**
     * Get menus by module ID.
     */
    List<MenuResponse> getMenusByModuleId(Long moduleId);

    /**
     * Update menu.
     */
    MenuResponse updateMenu(Long id, UpdateMenuRequest request);

    /**
     * Delete menu (soft delete).
     */
    void deleteMenu(Long id);

    /**
     * Count active menus.
     */
    long countActiveMenus();
}
