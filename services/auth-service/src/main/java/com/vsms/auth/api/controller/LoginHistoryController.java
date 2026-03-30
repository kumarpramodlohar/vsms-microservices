package com.vsms.auth.api.controller;

import com.vsms.auth.api.dto.CreateLoginHistoryRequest;
import com.vsms.auth.api.dto.LoginHistoryResponse;
import com.vsms.auth.application.service.LoginHistoryService;
import com.vsms.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for login history management.
 */
@RestController
@RequestMapping("/api/v1/auth/login-history")
@RequiredArgsConstructor
@Tag(name = "Login History Management", description = "Login history tracking operations")
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryService;

    @PostMapping
    @Operation(summary = "Create login history", description = "Create a new login history entry")
    public ResponseEntity<ApiResponse<LoginHistoryResponse>> createLoginHistory(
            @Valid @RequestBody CreateLoginHistoryRequest request) {
        LoginHistoryResponse response = loginHistoryService.createLoginHistory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Login history created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get login history by ID", description = "Retrieve login history details by ID")
    public ResponseEntity<ApiResponse<LoginHistoryResponse>> getLoginHistoryById(@PathVariable Integer id) {
        LoginHistoryResponse response = loginHistoryService.getLoginHistoryById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get all login history", description = "Retrieve all login history entries")
    public ResponseEntity<ApiResponse<List<LoginHistoryResponse>>> getAllLoginHistory() {
        List<LoginHistoryResponse> response = loginHistoryService.getAllLoginHistory();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get login history by user ID", description = "Retrieve login history by user ID")
    public ResponseEntity<ApiResponse<List<LoginHistoryResponse>>> getLoginHistoryByUserId(
            @PathVariable String userId) {
        List<LoginHistoryResponse> response = loginHistoryService.getLoginHistoryByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "Get recent login history by user ID", description = "Retrieve recent login history by user ID")
    public ResponseEntity<ApiResponse<List<LoginHistoryResponse>>> getRecentLoginHistoryByUserId(
            @PathVariable String userId) {
        List<LoginHistoryResponse> response = loginHistoryService.getRecentLoginHistoryByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}/date-range")
    @Operation(summary = "Get login history by user ID and date range", description = "Retrieve login history by user ID between dates")
    public ResponseEntity<ApiResponse<List<LoginHistoryResponse>>> getLoginHistoryByUserIdAndDateRange(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<LoginHistoryResponse> response = loginHistoryService
                .getLoginHistoryByUserIdAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Count login history by user ID", description = "Get count of login history entries by user ID")
    public ResponseEntity<ApiResponse<Long>> countLoginHistoryByUserId(@PathVariable String userId) {
        long count = loginHistoryService.countLoginHistoryByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
