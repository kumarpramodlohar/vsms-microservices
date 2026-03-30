package com.vsms.auth.application.service.impl;

import com.vsms.auth.api.dto.CreateMenuRequest;
import com.vsms.auth.api.dto.MenuResponse;
import com.vsms.auth.api.dto.UpdateMenuRequest;
import com.vsms.auth.domain.entity.Menu;
import com.vsms.auth.domain.repository.MenuRepository;
import com.vsms.common.exception.ResourceNotFoundException;
import com.vsms.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    private Menu testMenu;
    private CreateMenuRequest createMenuRequest;
    private UpdateMenuRequest updateMenuRequest;

    @BeforeEach
    void setUp() {
        testMenu = new Menu();
        testMenu.setId(1L);
        testMenu.setMenuName("Dashboard");
        testMenu.setMenuUrl("/dashboard");
        testMenu.setMenuIcon("dashboard-icon");
        testMenu.setOrderBy(1);
        testMenu.setIsActive(true);
        testMenu.setCreatedAt(LocalDateTime.now());
        testMenu.setUpdatedAt(LocalDateTime.now());
        testMenu.setCreatedBy("system");
        testMenu.setUpdatedBy("system");

        createMenuRequest = new CreateMenuRequest();
        createMenuRequest.setMenuName("Dashboard");
        createMenuRequest.setMenuUrl("/dashboard");
        createMenuRequest.setMenuIcon("dashboard-icon");
        createMenuRequest.setOrderBy(1);
        createMenuRequest.setCreatedBy("system");

        updateMenuRequest = new UpdateMenuRequest();
        updateMenuRequest.setMenuName("Updated Dashboard");
        updateMenuRequest.setMenuUrl("/updated-dashboard");
        updateMenuRequest.setUpdatedBy("admin");
    }

    @Test
    void createMenu_Success() {
        when(menuRepository.existsByMenuNameIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenReturn(testMenu);

        MenuResponse response = menuService.createMenu(createMenuRequest);

        assertNotNull(response);
        assertEquals("Dashboard", response.getMenuName());
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    void createMenu_DuplicateName_ThrowsValidationException() {
        when(menuRepository.existsByMenuNameIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(true);

        assertThrows(ValidationException.class, () -> menuService.createMenu(createMenuRequest));
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    void getMenuById_Success() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(testMenu));

        MenuResponse response = menuService.getMenuById(1L);

        assertNotNull(response);
        assertEquals("Dashboard", response.getMenuName());
    }

    @Test
    void getMenuById_NotFound_ThrowsResourceNotFoundException() {
        when(menuRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuService.getMenuById(99L));
    }

    @Test
    void getAllMenus_Success() {
        Menu menu2 = new Menu();
        menu2.setId(2L);
        menu2.setMenuName("Settings");
        menu2.setIsActive(true);

        when(menuRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testMenu, menu2));

        List<MenuResponse> responses = menuService.getAllMenus();

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    @Test
    void getMenusByModuleId_Success() {
        when(menuRepository.findByModuleIdAndIsActiveTrue(1L)).thenReturn(Arrays.asList(testMenu));

        List<MenuResponse> responses = menuService.getMenusByModuleId(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getModuleId());
    }

    @Test
    void updateMenu_Success() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(testMenu));
        when(menuRepository.save(any(Menu.class))).thenReturn(testMenu);

        MenuResponse response = menuService.updateMenu(1L, updateMenuRequest);

        assertNotNull(response);
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    void updateMenu_NotFound_ThrowsResourceNotFoundException() {
        when(menuRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuService.updateMenu(99L, updateMenuRequest));
    }

    @Test
    void deleteMenu_Success() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(testMenu));
        when(menuRepository.save(any(Menu.class))).thenReturn(testMenu);

        menuService.deleteMenu(1L);

        verify(menuRepository, times(1)).save(any(Menu.class));
        assertFalse(testMenu.getIsActive());
    }

    @Test
    void deleteMenu_NotFound_ThrowsResourceNotFoundException() {
        when(menuRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuService.deleteMenu(99L));
    }

    @Test
    void countActiveMenus_Success() {
        when(menuRepository.countByIsActiveTrue()).thenReturn(5L);

        long count = menuService.countActiveMenus();

        assertEquals(5L, count);
    }
}
