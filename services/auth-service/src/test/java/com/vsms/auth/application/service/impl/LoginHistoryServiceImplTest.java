package com.vsms.auth.application.service.impl;

import com.vsms.auth.api.dto.CreateLoginHistoryRequest;
import com.vsms.auth.api.dto.LoginHistoryResponse;
import com.vsms.auth.application.service.LoginHistoryService;
import com.vsms.auth.domain.entity.LoginHistory;
import com.vsms.auth.domain.repository.LoginHistoryRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginHistoryServiceImplTest {

    @Mock
    private LoginHistoryRepository loginHistoryRepository;

    @InjectMocks
    private LoginHistoryServiceImpl loginHistoryService;

    private LoginHistory testLoginHistory;
    private CreateLoginHistoryRequest createRequest;

    @BeforeEach
    void setUp() {
        testLoginHistory = new LoginHistory();
        testLoginHistory.setId(1);
        testLoginHistory.setUserId("user123");
        testLoginHistory.setLoginDtTime(LocalDateTime.now());
        testLoginHistory.setLogoutDtTime(LocalDateTime.now().plusHours(1));
        testLoginHistory.setIsActive(true);

        createRequest = new CreateLoginHistoryRequest();
        createRequest.setUserId("user123");
        createRequest.setLoginDtTime(LocalDateTime.now());
        createRequest.setLogoutDtTime(LocalDateTime.now().plusHours(1));
    }

    @Test
    void createLoginHistory_Success() {
        when(loginHistoryRepository.save(any(LoginHistory.class))).thenReturn(testLoginHistory);

        LoginHistoryResponse response = loginHistoryService.createLoginHistory(createRequest);

        assertNotNull(response);
        assertEquals("user123", response.getUserId());
        verify(loginHistoryRepository, times(1)).save(any(LoginHistory.class));
    }

    @Test
    void getLoginHistoryById_Success() {
        when(loginHistoryRepository.findById(1)).thenReturn(Optional.of(testLoginHistory));

        LoginHistoryResponse response = loginHistoryService.getLoginHistoryById(1);

        assertNotNull(response);
        assertEquals("user123", response.getUserId());
    }

    @Test
    void getLoginHistoryById_NotFound() {
        when(loginHistoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> loginHistoryService.getLoginHistoryById(99));
    }

    @Test
    void getAllLoginHistory_Success() {
        when(loginHistoryRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testLoginHistory));

        List<LoginHistoryResponse> response = loginHistoryService.getAllLoginHistory();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getLoginHistoryByUserId_Success() {
        when(loginHistoryRepository.findByUserIdAndIsActiveTrue("user123")).thenReturn(Arrays.asList(testLoginHistory));

        List<LoginHistoryResponse> response = loginHistoryService.getLoginHistoryByUserId("user123");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getLoginHistoryByUserIdAndDateRange_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(loginHistoryRepository.findByUserIdAndLoginDtTimeBetweenAndIsActiveTrue("user123", startDate, endDate))
                .thenReturn(Arrays.asList(testLoginHistory));

        List<LoginHistoryResponse> response = loginHistoryService.getLoginHistoryByUserIdAndDateRange("user123", startDate, endDate);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRecentLoginHistoryByUserId_Success() {
        when(loginHistoryRepository.findRecentByUserId("user123")).thenReturn(Arrays.asList(testLoginHistory));

        List<LoginHistoryResponse> response = loginHistoryService.getRecentLoginHistoryByUserId("user123");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void countLoginHistoryByUserId_Success() {
        when(loginHistoryRepository.countByUserIdAndIsActiveTrue("user123")).thenReturn(5L);

        long count = loginHistoryService.countLoginHistoryByUserId("user123");

        assertEquals(5L, count);
    }
}
