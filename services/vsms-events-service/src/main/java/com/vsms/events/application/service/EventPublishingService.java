package com.vsms.events.application.service;

import com.vsms.events.domain.entity.EventStore;

import java.util.concurrent.CompletableFuture;

/**
 * Service interface for publishing domain events to the event store and message broker.
 *
 * This service provides the core functionality for event publishing in the VSMS event-driven
 * architecture. It handles both synchronous database persistence and asynchronous message
 * publishing to Kafka, ensuring reliable event distribution across the microservices ecosystem.
 *
 * Key Responsibilities:
 * - Event validation and enrichment with metadata
 * - Persistent storage in event store for audit trails
 * - Asynchronous publishing to Kafka for real-time processing
 * - Correlation ID management for distributed tracing
 * - Error handling and retry logic for failed publications
 *
 * Design Patterns:
 * - Command pattern for event publishing operations
 * - Observer pattern for event processing notifications
 * - Circuit breaker for resilient message broker communication
 *
 * @author VSMS Development Team
 * @version 1.0.0
 * @since 2026-03-30
 */
public interface EventPublishingService {

    /**
     * Publishes a domain event synchronously to the event store and asynchronously to Kafka.
     *
     * This method ensures the event is first persisted to the database for consistency,
     * then published to Kafka for real-time processing by other services. The operation
     * is designed to be reliable with proper error handling and retry mechanisms.
     *
     * Process Flow:
     * 1. Validate event data and required fields
     * 2. Enrich event with metadata (correlation ID, source service, etc.)
     * 3. Persist event to database (synchronous)
     * 4. Publish to Kafka topic (asynchronous)
     * 5. Update processing status based on success/failure
     *
     * @param event The domain event to publish (from shared vsms-events library)
     * @param aggregateType The type of aggregate this event belongs to
     * @param aggregateId The unique identifier of the aggregate
     * @param aggregateVersion The version of the aggregate for optimistic locking
     * @param correlationId Optional correlation ID for distributed tracing
     * @param causationId Optional causation ID referencing the triggering event
     * @return CompletableFuture that completes when publishing is done
     * @throws EventPublishingException if validation fails or publishing encounters errors
     */
    CompletableFuture<Void> publishEvent(
        Object event,
        String aggregateType,
        String aggregateId,
        Long aggregateVersion,
        String correlationId,
        String causationId
    );

    /**
     * Publishes an event with automatic aggregate version detection.
     *
     * This convenience method automatically determines the next version number for the aggregate
     * by querying the latest event in the event store. Useful for scenarios where version
     * management is handled by the service rather than the caller.
     *
     * @param event The domain event to publish
     * @param aggregateType The type of aggregate
     * @param aggregateId The aggregate identifier
     * @param correlationId Optional correlation ID
     * @param causationId Optional causation ID
     * @return CompletableFuture that completes when publishing is done
     * @throws EventPublishingException if publishing fails
     */
    CompletableFuture<Void> publishEventWithAutoVersioning(
        Object event,
        String aggregateType,
        String aggregateId,
        String correlationId,
        String causationId
    );

    /**
     * Publishes a system event not related to a specific aggregate.
     *
     * Used for publishing infrastructure events, monitoring events, or broadcast messages
     * that don't belong to a specific business aggregate but need to be tracked.
     *
     * @param event The system event to publish
     * @param eventType The type identifier for the event
     * @param correlationId Optional correlation ID
     * @return CompletableFuture that completes when publishing is done
     * @throws EventPublishingException if publishing fails
     */
    CompletableFuture<Void> publishSystemEvent(
        Object event,
        String eventType,
        String correlationId
    );

    /**
     * Republishes a stored event to Kafka without creating a duplicate in the database.
     *
     * Useful for retry scenarios or when an event needs to be reprocessed by consumers
     * that may have missed it initially. The event must already exist in the event store.
     *
     * @param eventId The unique identifier of the event to republish
     * @return CompletableFuture that completes when republishing is done
     * @throws EventPublishingException if event not found or republishing fails
     */
    CompletableFuture<Void> republishEvent(String eventId);

    /**
     * Validates an event before publishing.
     *
     * Performs structural validation on the event object to ensure it meets the
     * requirements for publishing (required fields, valid data types, etc.).
     *
     * @param event The event to validate
     * @param aggregateType The aggregate type context
     * @throws EventPublishingException if validation fails
     */
    void validateEvent(Object event, String aggregateType);

    /**
     * Custom exception for event publishing failures.
     *
     * Provides detailed error information for debugging publishing issues,
     * including validation errors, database errors, and message broker errors.
     */
    class EventPublishingException extends RuntimeException {

        private final String eventType;
        private final String aggregateId;
        private final ErrorType errorType;

        public enum ErrorType {
            VALIDATION_ERROR,
            DATABASE_ERROR,
            KAFKA_ERROR,
            SERIALIZATION_ERROR,
            UNKNOWN_ERROR
        }

        public EventPublishingException(String message, ErrorType errorType) {
            super(message);
            this.errorType = errorType;
            this.eventType = null;
            this.aggregateId = null;
        }

        public EventPublishingException(String message, ErrorType errorType,
                                      String eventType, String aggregateId) {
            super(message);
            this.errorType = errorType;
            this.eventType = eventType;
            this.aggregateId = aggregateId;
        }

        public EventPublishingException(String message, ErrorType errorType, Throwable cause) {
            super(message, cause);
            this.errorType = errorType;
            this.eventType = null;
            this.aggregateId = null;
        }

        // Getters
        public String getEventType() { return eventType; }
        public String getAggregateId() { return aggregateId; }
        public ErrorType getErrorType() { return errorType; }
    }
}