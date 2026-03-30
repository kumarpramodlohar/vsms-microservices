package com.vsms.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

/**
 * Global error handler for API Gateway.
 */
@Configuration
@RestControllerAdvice
@Order(-2)
public class GlobalErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalErrorHandler.class);

    // Global error handling is configured through Spring Cloud Gateway defaults
    // Circuit breaker fallbacks provide custom error responses for individual routes

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception ex) {
        logger.error("Unhandled exception in gateway", ex);
        return Map.of(
                "success", false,
                "message", "Internal server error",
                "timestamp", System.currentTimeMillis()
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Map<String, Object> handleResponseStatusException(ResponseStatusException ex) {
        logger.warn("Response status exception: {}", ex.getMessage());
        return Map.of(
                "success", false,
                "message", ex.getReason() != null ? ex.getReason() : "Bad request",
                "status", ex.getStatusCode().value(),
                "timestamp", System.currentTimeMillis()
        );
    }
}