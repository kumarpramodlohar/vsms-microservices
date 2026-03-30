package com.vsms.auth.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsms.auth.api.dto.CreateMenuRequest;
import com.vsms.auth.api.dto.MenuResponse;
import com.vsms.auth.api.dto.UpdateMenuRequest;
import com.vsms.auth.application.service.MenuService;
import com.vsms.common.exception.ResourceNotFoundException;
import com.vsms.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MenuControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController menuController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MenuResponse testMenuResponse;
    private CreateMenuRequest createMenuRequest;
    private UpdateMenuRequest updateMenuRequest;

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();

        testMenuResponse = MenuResponse.builder()
                .id(1L)
                .menuName("Dashboard")
                .menuUrl("/dashboard")
                .menuIcon("dashboard")
                .orderBy(1)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build();

        createMenuRequest = CreateMenuRequest.builder()
                .menuName("Dashboard")
                .menuUrl("/dashboard")
                .menuIcon("dashboard")
                .orderBy(1)
                .createdBy("system")
                .build();

        updateMenuRequest = UpdateMenuRequest.builder()
                .menuName("Dashboard Updated")
                .menuUrl("/dashboard-updated")
                .menuIcon("dashboard-updated")
                .orderBy(2)
                .updatedBy("admin")
                .build();
    }

    @Test
    void createMenu_Success() throws Exception {
        when(menuService.createMenu(any(CreateMenuRequest.class))).thenReturn(testMenuResponse);

        mockMvc.perform(post("/api/v1/auth/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMenuRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.menuName", is("Dashboard")));

        verify(menuService, times(1)).createMenu(any(CreateMenuRequest.class));
    }

    @Test
    void createMenu_DuplicateMenuName_ThrowsException() throws Exception {
        when(menuService.createMenu(any(CreateMenuRequest.class)))
                .thenThrow(new ValidationException("Menu name already exists"));

        mockMvc.perform(post("/api/v1/auth/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMenuRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));

        verify(menuService, times(1)).createMenu(any(CreateMenuRequest.class));
    }

    @Test
    void getMenuById_Success() throws Exception {
        when(menuService.getMenuById(eq(1L))).thenReturn(testMenuResponse);

        mockMvc.perform(get("/api/v1/auth/menus/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.menuName", is("Dashboard")));

        verify(menuService, times(1)).getMenuById(eq(1L));
    }

    @Test
    void getMenuById_NotFound_ThrowsException() throws Exception {
        when(menuService.getMenuById(eq(999L)))
                .thenThrow(new ResourceNotFoundException("Menu not found"));

        mockMvc.perform(get("/api/v1/auth/menus/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(menuService, times(1)).getMenuById(eq(999L));
    }

    @Test
    void getMenuByMenuName_Success() throws Exception {
        when(menuService.getMenuByMenuName(eq("Dashboard"))).thenReturn(testMenuResponse);

        mockMvc.perform(get("/api/v1/auth/menus/name/{menuName}", "Dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.menuName", is("Dashboard")));

        verify(menuService, times(1)).getMenuByMenuName(eq("Dashboard"));
    }

    @Test
    void getAllMenus_Success() throws Exception {
        List<MenuResponse> menus = Arrays.asList(testMenuResponse);
        when(menuService.getAllMenus()).thenReturn(menus);

        mockMvc.perform(get("/api/v1/auth/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].menuName", is("Dashboard")));

        verify(menuService, times(1)).getAllMenus();
    }

    @Test
    void getAllActiveMenus_Success() throws Exception {
        List<MenuResponse> menus = Arrays.asList(testMenuResponse);
        when(menuService.getAllActiveMenus()).thenReturn(menus);

        mockMvc.perform(get("/api/v1/auth/menus/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].menuName", is("Dashboard")));

        verify(menuService, times(1)).getAllActiveMenus();
    }

    @Test
    void getMenusByModuleId_Success() throws Exception {
        List<MenuResponse> menus = Arrays.asList(testMenuResponse);
        when(menuService.getMenusByModuleId(eq(1L))).thenReturn(menus);

        mockMvc.perform(get("/api/v1/auth/menus/module/{moduleId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].menuName", is("Dashboard")));

        verify(menuService, times(1)).getMenusByModuleId(eq(1L));
    }

    @Test
    void updateMenu_Success() throws Exception {
        when(menuService.updateMenu(eq(1L), any(UpdateMenuRequest.class))).thenReturn(testMenuResponse);

        mockMvc.perform(put("/api/v1/auth/menus/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMenuRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.menuName", is("Dashboard")));

        verify(menuService, times(1)).updateMenu(eq(1L), any(UpdateMenuRequest.class));
    }

    @Test
    void updateMenu_NotFound_ThrowsException() throws Exception {
        when(menuService.updateMenu(eq(999L), any(UpdateMenuRequest.class)))
                .thenThrow(new ResourceNotFoundException("Menu not found"));

        mockMvc.perform(put("/api/v1/auth/menus/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMenuRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(menuService, times(1)).updateMenu(eq(999L), any(UpdateMenuRequest.class));
    }

    @Test
    void deleteMenu_Success() throws Exception {
        doNothing().when(menuService).deleteMenu(eq(1L));

        mockMvc.perform(delete("/api/v1/auth/menus/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(menuService, times(1)).deleteMenu(eq(1L));
    }

    @Test
    void deleteMenu_NotFound_ThrowsException() throws Exception {
        doThrow(new ResourceNotFoundException("Menu not found"))
                .when(menuService).deleteMenu(eq(999L));

        mockMvc.perform(delete("/api/v1/auth/menus/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(menuService, times(1)).deleteMenu(eq(999L));
    }

    @Test
    void countActiveMenus_Success() throws Exception {
        when(menuService.countActiveMenus()).thenReturn(5L);

        mockMvc.perform(get("/api/v1/auth/menus/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is(5)));

        verify(menuService, times(1)).countActiveMenus();
    }
}
