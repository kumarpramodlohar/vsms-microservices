package com.vsms.master.config;

// AUTO-GENERATED: application config beans for master-service
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ServiceConfig {

    // AUTO-GENERATED: circuit breaker config — tune thresholds before production
    @Bean
    public CircuitBreakerConfig defaultCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .slidingWindowSize(10)
                .build();
    }

    // AUTO-GENERATED: simple in-memory cache — master data is read-heavy, cache aggressively
    // Replace with Redis for distributed/multi-instance deployments
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("items", "companies", "locations", "uom", "categories", "states", "countries");
    }
}