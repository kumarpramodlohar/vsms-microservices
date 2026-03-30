package com.vsms.auth.application.service.impl;

import com.vsms.auth.api.dto.CreateLoginHistoryRequest;
import com.vsms.auth.api.dto.LoginHistoryResponse;
import com.vsms.auth.application.service.LoginHistoryService;
import com.vsms.auth.domain.entity.LoginHistory;
import com.vsms.auth.domain.repository.LoginHistoryRepository;
import com.vsms.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LoginHistory service implementation.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    public LoginHistoryResponse createLoginHistory(CreateLoginHistoryRequest request) {
        log.info("Creating new login history entry for userId: {}", request.getUserId());

        // Create new login history entry
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUserId(request.getUserId());
        loginHistory.setMacAddr(request.getMacAddr());
        loginHistory.setLoginDtTime(request.getLoginDtTime());
        loginHistory.setLogoutDtTime(request.getLogoutDtTime());
        loginHistory.setIsActive(true);
        loginHistory.setCreatedBy(request.getCreatedBy());
        loginHistory.setUpdatedBy(request.getCreatedBy());

        LoginHistory savedLoginHistory = loginHistoryRepository.save(loginHistory);
        log.info("Login history entry created successfully with ID: {}", savedLoginHistory.getId());

        return mapToLoginHistoryResponse(savedLoginHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginHistoryResponse getLoginHistoryById(Integer id) {
        log.info("Fetching login history by ID: {}", id);

        LoginHistory loginHistory = loginHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Login history not found with ID: " + id));

        return mapToLoginHistoryResponse(loginHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginHistoryResponse> getAllLoginHistory() {
        log.info("Fetching all login history entries");

        List<LoginHistory> loginHistoryEntries = loginHistoryRepository.findByIsActiveTrue();
        return loginHistoryEntries.stream()
                .map(this::mapToLoginHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginHistoryResponse> getLoginHistoryByUserId(String userId) {
        log.info("Fetching login history by userId: {}", userId);

        List<LoginHistory> loginHistoryEntries = loginHistoryRepository.findByUserIdAndIsActiveTrue(userId);
        return loginHistoryEntries.stream()
                .map(this::mapToLoginHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginHistoryResponse> getLoginHistoryByUserIdAndDateRange(
            String userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching login history by userId: {} between dates: {} and {}", userId, startDate, endDate);

        List<LoginHistory> loginHistoryEntries = loginHistoryRepository
                .findByUserIdAndLoginDtTimeBetweenAndIsActiveTrue(userId, startDate, endDate);
        return loginHistoryEntries.stream()
                .map(this::mapToLoginHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginHistoryResponse> getRecentLoginHistoryByUserId(String userId) {
        log.info("Fetching recent login history by userId: {}", userId);

        List<LoginHistory> loginHistoryEntries = loginHistoryRepository.findRecentByUserId(userId);
        return loginHistoryEntries.stream()
                .map(this::mapToLoginHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countLoginHistoryByUserId(String userId) {
        log.info("Counting login history by userId: {}", userId);
        return loginHistoryRepository.countByUserIdAndIsActiveTrue(userId);
    }

    /**
     * Map LoginHistory entity to LoginHistoryResponse DTO.
     */
    private LoginHistoryResponse mapToLoginHistoryResponse(LoginHistory loginHistory) {
        return LoginHistoryResponse.builder()
                .id(loginHistory.getId() != null ? loginHistory.getId().longValue() : null)
                .userId(loginHistory.getUserId())
                .macAddr(loginHistory.getMacAddr())
                .loginDtTime(loginHistory.getLoginDtTime())
                .logoutDtTime(loginHistory.getLogoutDtTime())
                .isActive(loginHistory.getIsActive())
                .createdAt(loginHistory.getCreatedAt())
                .updatedAt(loginHistory.getUpdatedAt())
                .createdBy(loginHistory.getCreatedBy())
                .updatedBy(loginHistory.getUpdatedBy())
                .build();
    }
}
