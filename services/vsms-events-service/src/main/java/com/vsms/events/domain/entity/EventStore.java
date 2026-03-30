package com.vsms.events.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * EventStore entity represents the persistent storage of domain events in the event sourcing pattern.
 *
 * This entity stores all business events that occur within the VSMS ecosystem for audit trails,
 * analytics, and event replay capabilities. Each event is stored with its metadata and payload
 * in a serialized JSON format.
 *
 * Key Design Principles:
 * - Event Sourcing: All events are stored immutably for complete audit trail
 * - Generic Storage: Events are stored as JSON payloads with type information
 * - Partitioning: Events are partitioned by aggregate type for efficient querying
 * - Indexing: Optimized indexes for common query patterns (timestamp, event type, aggregate)
 *
 * Database Design:
 * - Uses UUID for global uniqueness across distributed systems
 * - JSON column for flexible event payload storage
 * - Composite indexes for query performance
 * - Partitioning strategy for large-scale event storage
 *
 * @author VSMS Development Team
 * @version 1.0.0
 * @since 2026-03-30
 */
@Entity
@Table(name = "event_store", indexes = {
    @Index(name = "idx_event_store_aggregate", columnList = "aggregateType, aggregateId"),
    @Index(name = "idx_event_store_type_timestamp", columnList = "eventType, timestamp"),
    @Index(name = "idx_event_store_timestamp", columnList = "timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventStore {

    /**
     * Unique identifier for the event. Uses UUID v7 for time-ordered uniqueness
     * across distributed systems and better index performance.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id", updatable = false, nullable = false)
    private UUID eventId;

    /**
     * Type of aggregate this event belongs to (e.g., "Customer", "SalesOrder", "Inventory").
     * Used for partitioning and filtering events by business domain.
     */
    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    /**
     * Unique identifier of the aggregate instance (e.g., customerId, salesOrderId).
     * Links events to specific business entities.
     */
    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    /**
     * Type of the event (e.g., "CustomerApproved", "SalesOrderActivated").
     * Used for event routing, filtering, and processing logic.
     */
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    /**
     * Version of the aggregate at the time this event was generated.
     * Ensures event ordering and prevents concurrent modification issues.
     */
    @Column(name = "aggregate_version", nullable = false)
    private Long aggregateVersion;

    /**
     * The event payload stored as JSON string.
     * Contains the complete event data in a structured format.
     * Uses TEXT column for flexibility with complex event structures.
     */
    @Column(name = "event_data", nullable = false, columnDefinition = "TEXT")
    private String eventData;

    /**
     * Additional metadata about the event as JSON string.
     * May include correlation IDs, user context, source service, etc.
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    /**
     * Timestamp when the event was created.
     * Automatically set by the database and used for event ordering and time-based queries.
     */
    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    /**
     * Identifier of the service that published this event.
     * Useful for debugging, monitoring, and understanding event provenance.
     */
    @Column(name = "source_service", length = 100)
    private String sourceService;

    /**
     * Correlation ID for tracing related events across services.
     * Links events that are part of the same business transaction or workflow.
     */
    @Column(name = "correlation_id")
    private String correlationId;

    /**
     * Causation ID referencing the event that caused this event.
     * Useful for understanding event causality chains.
     */
    @Column(name = "causation_id")
    private String causationId;

    /**
     * Status of event processing (PENDING, PROCESSED, FAILED).
     * Used by event processors to track processing state.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false)
    @Builder.Default
    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;

    /**
     * Timestamp when the event was successfully processed.
     * Null until processing is complete.
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    /**
     * Error message if event processing failed.
     * Used for debugging and retry logic.
     */
    @Column(name = "processing_error", columnDefinition = "TEXT")
    private String processingError;

    /**
     * Number of processing attempts.
     * Used for retry logic and circuit breaker patterns.
     */
    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    /**
     * Enumeration of possible event processing statuses.
     */
    public enum ProcessingStatus {
        /**
         * Event is waiting to be processed.
         */
        PENDING,

        /**
         * Event has been successfully processed.
         */
        PROCESSED,

        /**
         * Event processing failed and may be retried.
         */
        FAILED,

        /**
         * Event processing failed permanently and will not be retried.
         */
        DEAD_LETTER
    }
}