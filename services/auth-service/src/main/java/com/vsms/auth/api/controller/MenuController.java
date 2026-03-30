package com.vsms.auth.api.controller;

import com.vsms.auth.api.dto.CreateMenuRequest;
import com.vsms.auth.api.dto.MenuResponse;
import com.vsms.auth.api.dto.UpdateMenuRequest;
import com.vsms.auth.application.service.MenuService;
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
 * REST controller for menu management.
 */
@RestController
@RequestMapping("/api/v1/auth/menus")
@RequiredArgsConstructor
@Tag(name = "Menu Management", description = "Menu CRUD operations")
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    @Operation(summary = "Create menu", description = "Create a new menu")
    public ResponseEntity<ApiResponse<MenuResponse>> createMenu(@Valid @RequestBody CreateMenuRequest request) {
        MenuResponse response = menuService.createMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Menu created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get menu by ID", description = "Retrieve menu details by ID")
    public ResponseEntity<ApiResponse<MenuResponse>> getMenuById(@PathVariable Long id) {
        MenuResponse response = menuService.getMenuById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/name/{menuName}")
    @Operation(summary = "Get menu by name", description = "Retrieve menu details by menu name")
    public ResponseEntity<ApiResponse<MenuResponse>> getMenuByMenuName(@PathVariable String menuName) {
        MenuResponse response = menuService.getMenuByMenuName(menuName);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get all menus", description = "Retrieve all menus")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getAllMenus() {
        List<MenuResponse> response = menuService.getAllMenus();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active menus", description = "Retrieve all active menus")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getAllActiveMenus() {
        List<MenuResponse> response = menuService.getAllActiveMenus();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Get menus by module ID", description = "Retrieve menus by module ID")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getMenusByModuleId(@PathVariable Long moduleId) {
        List<MenuResponse> response = menuService.getMenusByModuleId(moduleId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update menu", description = "Update menu details")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenu(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuRequest request) {
        MenuResponse response = menuService.updateMenu(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Menu updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete menu", description = "Soft delete menu (set isActive to false)")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Menu deleted successfully"));
    }

    @GetMapping("/count")
    @Operation(summary = "Count active menus", description = "Get count of active menus")
    public ResponseEntity<ApiResponse<Long>> countActiveMenus() {
        long count = menuService.countActiveMenus();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
