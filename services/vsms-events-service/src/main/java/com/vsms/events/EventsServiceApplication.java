package com.vsms.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for the VSMS Events Service.
 *
 * This service provides a comprehensive event-driven infrastructure for the VSMS microservices ecosystem.
 * It handles event publishing, persistence, streaming, and consumption to enable real-time business
 * process monitoring and automated workflows.
 *
 * Key Features:
 * - Event Publishing Framework: Standardized event publishing across all microservices
 * - Event Persistence Layer: Stores events in database for audit trail and replay capabilities
 * - Event Streaming Platform: Uses Kafka for reliable event streaming and processing
 * - Event Querying: REST APIs for retrieving historical events and analytics
 *
 * Architecture:
 * - Built on Spring Boot with JPA for data persistence
 * - Kafka integration for event streaming and messaging
 * - Eureka client for service discovery
 * - OpenAPI/Swagger for API documentation
 * - Comprehensive security with OAuth2
 *
 * @author VSMS Development Team
 * @version 1.0.0
 * @since 2026-03-30
 */
@SpringBootApplication
@EnableFeignClients
@EnableKafka
public class EventsServiceApplication {

    /**
     * Main entry point for the Events Service application.
     *
     * Initializes the Spring Boot application context and starts all configured components
     * including web server, database connections, Kafka consumers/producers, and Eureka registration.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(EventsServiceApplication.class, args);
    }
}