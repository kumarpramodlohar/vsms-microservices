package com.vsms.auth.application.service;

import com.vsms.auth.api.dto.CreateLoginHistoryRequest;
import com.vsms.auth.api.dto.LoginHistoryResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LoginHistory service interface.
 */
public interface LoginHistoryService {

    /**
     * Create a new login history entry.
     */
    LoginHistoryResponse createLoginHistory(CreateLoginHistoryRequest request);

    /**
     * Get login history by ID.
     */
    LoginHistoryResponse getLoginHistoryById(Integer id);

    /**
     * Get all login history entries.
     */
    List<LoginHistoryResponse> getAllLoginHistory();

    /**
     * Get login history by user ID.
     */
    List<LoginHistoryResponse> getLoginHistoryByUserId(String userId);

    /**
     * Get login history by user ID between dates.
     */
    List<LoginHistoryResponse> getLoginHistoryByUserIdAndDateRange(
            String userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get recent login history by user ID.
     */
    List<LoginHistoryResponse> getRecentLoginHistoryByUserId(String userId);

    /**
     * Count login history by user ID.
     */
    long countLoginHistoryByUserId(String userId);
}
