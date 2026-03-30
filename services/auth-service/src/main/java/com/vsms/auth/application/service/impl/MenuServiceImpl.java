package com.vsms.auth.application.service.impl;

import com.vsms.auth.api.dto.CreateMenuRequest;
import com.vsms.auth.api.dto.MenuResponse;
import com.vsms.auth.api.dto.UpdateMenuRequest;
import com.vsms.auth.application.service.MenuService;
import com.vsms.auth.domain.entity.Menu;
import com.vsms.auth.domain.repository.MenuRepository;
import com.vsms.common.exception.ResourceNotFoundException;
import com.vsms.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Menu service implementation.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    @Override
    public MenuResponse createMenu(CreateMenuRequest request) {
        log.info("Creating new menu with menuName: {}", request.getMenuName());

        // Check if menu already exists
        if (menuRepository.existsByMenuNameIgnoreCaseAndIsActiveTrue(request.getMenuName())) {
            throw new ValidationException("Menu with name '" + request.getMenuName() + "' already exists");
        }

        // Create new menu
        Menu menu = new Menu();
        menu.setMenuName(request.getMenuName());
        menu.setModuleId(request.getModuleId());
        menu.setMenuUrl(request.getMenuUrl());
        menu.setMenuIcon(request.getMenuIcon());
        menu.setOrderBy(request.getOrderBy());
        menu.setIsActive(true);
        menu.setCreatedBy(request.getCreatedBy());
        menu.setUpdatedBy(request.getCreatedBy());

        Menu savedMenu = menuRepository.save(menu);
        log.info("Menu created successfully with ID: {}", savedMenu.getId());

        return mapToMenuResponse(savedMenu);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponse getMenuById(Long id) {
        log.info("Fetching menu by ID: {}", id);

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with ID: " + id));

        return mapToMenuResponse(menu);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponse getMenuByMenuName(String menuName) {
        log.info("Fetching menu by menuName: {}", menuName);

        Menu menu = menuRepository.findByMenuNameAndIsActiveTrue(menuName)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with menuName: " + menuName));

        return mapToMenuResponse(menu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponse> getAllActiveMenus() {
        log.info("Fetching all active menus");

        List<Menu> menus = menuRepository.findByIsActiveTrue();
        return menus.stream()
                .map(this::mapToMenuResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponse> getAllMenus() {
        log.info("Fetching all menus");

        List<Menu> menus = menuRepository.findAll();
        return menus.stream()
                .map(this::mapToMenuResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponse> getMenusByModuleId(Long moduleId) {
        log.info("Fetching menus by moduleId: {}", moduleId);

        List<Menu> menus = menuRepository.findByModuleIdAndIsActiveTrue(moduleId);
        return menus.stream()
                .map(this::mapToMenuResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MenuResponse updateMenu(Long id, UpdateMenuRequest request) {
        log.info("Updating menu with ID: {}", id);

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with ID: " + id));

        // Update fields if provided
        if (request.getMenuName() != null) {
            // Check if menu name already exists for another menu
            if (!menu.getMenuName().equalsIgnoreCase(request.getMenuName()) &&
                    menuRepository.existsByMenuNameIgnoreCaseAndIsActiveTrue(request.getMenuName())) {
                throw new ValidationException("Menu with name '" + request.getMenuName() + "' already exists");
            }
            menu.setMenuName(request.getMenuName());
        }
        if (request.getModuleId() != null) {
            menu.setModuleId(request.getModuleId());
        }
        if (request.getMenuUrl() != null) {
            menu.setMenuUrl(request.getMenuUrl());
        }
        if (request.getMenuIcon() != null) {
            menu.setMenuIcon(request.getMenuIcon());
        }
        if (request.getOrderBy() != null) {
            menu.setOrderBy(request.getOrderBy());
        }

        if (request.getUpdatedBy() != null) {
            menu.setUpdatedBy(request.getUpdatedBy());
        }

        Menu updatedMenu = menuRepository.save(menu);
        log.info("Menu updated successfully with ID: {}", updatedMenu.getId());

        return mapToMenuResponse(updatedMenu);
    }

    @Override
    public void deleteMenu(Long id) {
        log.info("Deleting menu with ID: {}", id);

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with ID: " + id));

        // Soft delete
        menu.setIsActive(false);
        menuRepository.save(menu);

        log.info("Menu soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveMenus() {
        log.info("Counting active menus");
        return menuRepository.countByIsActiveTrue();
    }

    /**
     * Map Menu entity to MenuResponse DTO.
     */
    private MenuResponse mapToMenuResponse(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .menuName(menu.getMenuName())
                .moduleId(menu.getModuleId())
                .menuUrl(menu.getMenuUrl())
                .menuIcon(menu.getMenuIcon())
                .orderBy(menu.getOrderBy())
                .isActive(menu.getIsActive())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .createdBy(menu.getCreatedBy())
                .updatedBy(menu.getUpdatedBy())
                .build();
    }
}
