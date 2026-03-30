package com.vsms.drs.api.controller;

import com.vsms.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dummy controller for DRS (Delivery Route Service) to test security configuration.
 * This will be removed after testing.
 */
@RestController
@RequestMapping("/api/v1/drs")
public class DrsController {

    @GetMapping("/test")
    public ApiResponse<String> testSecurity() {
        return ApiResponse.success("DRS service security test successful", "Security configuration is working!");
    }
}