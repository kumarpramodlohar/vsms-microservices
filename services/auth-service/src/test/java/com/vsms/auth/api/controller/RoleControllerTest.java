package com.vsms.auth.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsms.auth.api.dto.CreateRoleRequest;
import com.vsms.auth.api.dto.RoleResponse;
import com.vsms.auth.api.dto.UpdateRoleRequest;
import com.vsms.auth.application.service.RoleService;
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

class RoleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private RoleResponse testRoleResponse;
    private CreateRoleRequest createRoleRequest;
    private UpdateRoleRequest updateRoleRequest;

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();

        testRoleResponse = RoleResponse.builder()
                .id(1L)
                .roleName("ADMIN")
                .description("Administrator role")
                .active("Y")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build();

        createRoleRequest = CreateRoleRequest.builder()
                .roleName("USER")
                .description("User role")
                .createdBy("system")
                .build();

        updateRoleRequest = UpdateRoleRequest.builder()
                .description("Updated role description")
                .updatedBy("admin")
                .build();
    }

    @Test
    void createRole_Success() throws Exception {
        when(roleService.createRole(any(CreateRoleRequest.class))).thenReturn(testRoleResponse);

        mockMvc.perform(post("/api/v1/auth/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRoleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.roleName", is("ADMIN")));

        verify(roleService, times(1)).createRole(any(CreateRoleRequest.class));
    }

    @Test
    void createRole_DuplicateRoleName_ThrowsException() throws Exception {
        when(roleService.createRole(any(CreateRoleRequest.class)))
                .thenThrow(new ValidationException("Role name already exists"));

        mockMvc.perform(post("/api/v1/auth/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRoleRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));

        verify(roleService, times(1)).createRole(any(CreateRoleRequest.class));
    }

    @Test
    void getRoleById_Success() throws Exception {
        when(roleService.getRoleById(eq(1L))).thenReturn(testRoleResponse);

        mockMvc.perform(get("/api/v1/auth/roles/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.roleName", is("ADMIN")));

        verify(roleService, times(1)).getRoleById(eq(1L));
    }

    @Test
    void getRoleById_NotFound_ThrowsException() throws Exception {
        when(roleService.getRoleById(eq(999L)))
                .thenThrow(new ResourceNotFoundException("Role not found"));

        mockMvc.perform(get("/api/v1/auth/roles/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(roleService, times(1)).getRoleById(eq(999L));
    }

    @Test
    void getRoleByRoleName_Success() throws Exception {
        when(roleService.getRoleByRoleName(eq("ADMIN"))).thenReturn(testRoleResponse);

        mockMvc.perform(get("/api/v1/auth/roles/name/{roleName}", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.roleName", is("ADMIN")));

        verify(roleService, times(1)).getRoleByRoleName(eq("ADMIN"));
    }

    @Test
    void getAllRoles_Success() throws Exception {
        List<RoleResponse> roles = Arrays.asList(testRoleResponse);
        when(roleService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/v1/auth/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].roleName", is("ADMIN")));

        verify(roleService, times(1)).getAllRoles();
    }

    @Test
    void getAllActiveRoles_Success() throws Exception {
        List<RoleResponse> roles = Arrays.asList(testRoleResponse);
        when(roleService.getAllActiveRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/v1/auth/roles/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].roleName", is("ADMIN")));

        verify(roleService, times(1)).getAllActiveRoles();
    }

    @Test
    void updateRole_Success() throws Exception {
        when(roleService.updateRole(eq(1L), any(UpdateRoleRequest.class))).thenReturn(testRoleResponse);

        mockMvc.perform(put("/api/v1/auth/roles/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRoleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.roleName", is("ADMIN")));

        verify(roleService, times(1)).updateRole(eq(1L), any(UpdateRoleRequest.class));
    }

    @Test
    void updateRole_NotFound_ThrowsException() throws Exception {
        when(roleService.updateRole(eq(999L), any(UpdateRoleRequest.class)))
                .thenThrow(new ResourceNotFoundException("Role not found"));

        mockMvc.perform(put("/api/v1/auth/roles/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRoleRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(roleService, times(1)).updateRole(eq(999L), any(UpdateRoleRequest.class));
    }

    @Test
    void deleteRole_Success() throws Exception {
        doNothing().when(roleService).deleteRole(eq(1L));

        mockMvc.perform(delete("/api/v1/auth/roles/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(roleService, times(1)).deleteRole(eq(1L));
    }

    @Test
    void deleteRole_NotFound_ThrowsException() throws Exception {
        doThrow(new ResourceNotFoundException("Role not found"))
                .when(roleService).deleteRole(eq(999L));

        mockMvc.perform(delete("/api/v1/auth/roles/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(roleService, times(1)).deleteRole(eq(999L));
    }

    @Test
    void countActiveRoles_Success() throws Exception {
        when(roleService.countActiveRoles()).thenReturn(5L);

        mockMvc.perform(get("/api/v1/auth/roles/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is(5)));

        verify(roleService, times(1)).countActiveRoles();
    }
}
