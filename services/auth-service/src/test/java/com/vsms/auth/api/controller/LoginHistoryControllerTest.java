package com.vsms.auth.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsms.auth.api.dto.CreateLoginHistoryRequest;
import com.vsms.auth.api.dto.LoginHistoryResponse;
import com.vsms.auth.application.service.LoginHistoryService;
import com.vsms.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(LoginHistoryController.class)
class LoginHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginHistoryService loginHistoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginHistoryResponse testLoginHistoryResponse;
    private CreateLoginHistoryRequest createLoginHistoryRequest;

    @BeforeEach
    void setUp() {
        testLoginHistoryResponse = LoginHistoryResponse.builder()
                .id(1)
                .userId("user123")
                .loginDtTime(LocalDateTime.now())
                .logoutDtTime(LocalDateTime.now().plusHours(1))
                .active("Y")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build();

        createLoginHistoryRequest = CreateLoginHistoryRequest.builder()
                .userId("user123")
                .loginDtTime(LocalDateTime.now())
                .logoutDtTime(LocalDateTime.now().plusHours(1))
                .createdBy("system")
                .build();
    }

    @Test
    void createLoginHistory_Success() throws Exception {
        when(loginHistoryService.createLoginHistory(any(CreateLoginHistoryRequest.class))).thenReturn(testLoginHistoryResponse);

        mockMvc.perform(post("/api/v1/auth/login-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLoginHistoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.userId", is("user123")));

        verify(loginHistoryService, times(1)).createLoginHistory(any(CreateLoginHistoryRequest.class));
    }

    @Test
    void getLoginHistoryById_Success() throws Exception {
        when(loginHistoryService.getLoginHistoryById(any(Integer.class))).thenReturn(testLoginHistoryResponse);

        mockMvc.perform(get("/api/v1/auth/login-history/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.userId", is("user123")));

        verify(loginHistoryService, times(1)).getLoginHistoryById(any(Integer.class));
    }

    @Test
    void getLoginHistoryById_NotFound_ThrowsException() throws Exception {
        when(loginHistoryService.getLoginHistoryById(any(Integer.class)))
                .thenThrow(new ResourceNotFoundException("Login history not found"));

        mockMvc.perform(get("/api/v1/auth/login-history/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(loginHistoryService, times(1)).getLoginHistoryById(any(Integer.class));
    }

    @Test
    void getLoginHistoryByUserId_Success() throws Exception {
        List<LoginHistoryResponse> histories = Arrays.asList(testLoginHistoryResponse);
        when(loginHistoryService.getLoginHistoryByUserId(eq("user123"))).thenReturn(histories);

        mockMvc.perform(get("/api/v1/auth/login-history/user/{userId}", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].userId", is("user123")));

        verify(loginHistoryService, times(1)).getLoginHistoryByUserId(eq("user123"));
    }

    @Test
    void getLoginHistoryByUserIdAndDateRange_Success() throws Exception {
        List<LoginHistoryResponse> histories = Arrays.asList(testLoginHistoryResponse);
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(loginHistoryService.getLoginHistoryByUserIdAndDateRange(eq("user123"), any(), any())).thenReturn(histories);

        mockMvc.perform(get("/api/v1/auth/login-history/user/{userId}/date-range", "user123")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(loginHistoryService, times(1)).getLoginHistoryByUserIdAndDateRange(eq("user123"), any(), any());
    }

    @Test
    void getRecentLoginHistoryByUserId_Success() throws Exception {
        List<LoginHistoryResponse> histories = Arrays.asList(testLoginHistoryResponse);
        when(loginHistoryService.getRecentLoginHistoryByUserId(eq("user123"))).thenReturn(histories);

        mockMvc.perform(get("/api/v1/auth/login-history/user/{userId}/recent", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(loginHistoryService, times(1)).getRecentLoginHistoryByUserId(eq("user123"));
    }

    @Test
    void countLoginHistoryByUserId_Success() throws Exception {
        when(loginHistoryService.countLoginHistoryByUserId(eq("user123"))).thenReturn(10L);

        mockMvc.perform(get("/api/v1/auth/login-history/user/{userId}/count", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is(10)));

        verify(loginHistoryService, times(1)).countLoginHistoryByUserId(eq("user123"));
    }
}
