package com.vsms.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway configuration for routes, filters, and security.
 */
@Configuration
public class GatewayConfig {

    private static final Logger logger = LoggerFactory.getLogger(GatewayConfig.class);

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth service routes with JWT authentication
                .route("auth-service", r -> r
                        .path("/api/v1/auth/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("authCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/auth"))
                                .rewritePath("/api/v1/auth/(?<segment>.*)", "/api/v1/auth/${segment}")
                        )
                        .uri("lb://auth-service")
                )

                // Master service routes
                .route("master-service", r -> r
                        .path("/api/v1/master/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("masterCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/master"))
                        )
                        .uri("lb://master-service")
                )

                // Customer service routes
                .route("customer-service", r -> r
                        .path("/api/v1/customer/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("customerCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/customer"))
                        )
                        .uri("lb://customer-service")
                )

                // Sales service routes
                .route("sales-service", r -> r
                        .path("/api/v1/sales/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("salesCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/sales"))
                        )
                        .uri("lb://sales-service")
                )

                // Purchase service routes
                .route("purchase-service", r -> r
                        .path("/api/v1/purchase/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("purchaseCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/purchase"))
                        )
                        .uri("lb://purchase-service")
                )

                // Inventory service routes
                .route("inventory-service", r -> r
                        .path("/api/v1/inventory/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("inventoryCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/inventory"))
                        )
                        .uri("lb://inventory-service")
                )

                // Fulfilment service routes
                .route("fulfilment-service", r -> r
                        .path("/api/v1/fulfilment/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("fulfilmentCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/fulfilment"))
                        )
                        .uri("lb://fulfilment-service")
                )

                // Cost service routes
                .route("cost-service", r -> r
                        .path("/api/v1/cost/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("costCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/cost"))
                        )
                        .uri("lb://cost-service")
                )

                // HR service routes
                .route("hr-service", r -> r
                        .path("/api/v1/hr/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("hrCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/hr"))
                        )
                        .uri("lb://hr-service")
                )

                // DRS service routes
                .route("drs-service", r -> r
                        .path("/api/v1/drs/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("drsCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/drs"))
                        )
                        .uri("lb://drs-service")
                )
                .build();
    }

    @Bean
    public GlobalFilter securityHeadersFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().getHeaders().add("X-Content-Type-Options", "nosniff");
            exchange.getResponse().getHeaders().add("X-Frame-Options", "DENY");
            exchange.getResponse().getHeaders().add("X-XSS-Protection", "1; mode=block");
            exchange.getResponse().getHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            return chain.filter(exchange);
        };
    }

    @Bean
    public GlobalFilter loggingFilter() {
        return (exchange, chain) -> {
            logger.info("Request: {} {} from {}", exchange.getRequest().getMethod(),
                       exchange.getRequest().getURI(),
                       exchange.getRequest().getRemoteAddress());

            long startTime = System.currentTimeMillis();
            return chain.filter(exchange).doOnSuccess(v -> {
                long duration = System.currentTimeMillis() - startTime;
                logger.info("Response: {} {} - {}ms", exchange.getRequest().getMethod(),
                           exchange.getRequest().getURI(), duration);
            });
        };
    }
}